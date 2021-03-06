package org.zstack.test.kvm;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.CoreGlobalProperty;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.thread.AsyncThread;
import org.zstack.core.thread.SyncTask;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.core.thread.ThreadGlobalProperty;
import org.zstack.header.allocator.HostCapacityVO;
import org.zstack.header.configuration.InstanceOfferingInventory;
import org.zstack.header.host.HostInventory;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.image.ImageInventory;
import org.zstack.header.image.ImageVO;
import org.zstack.header.network.l2.L2NetworkInventory;
import org.zstack.header.network.l3.L3NetworkVO;
import org.zstack.header.network.l3.L3NetworkVO_;
import org.zstack.header.vm.VmInstanceVO;
import org.zstack.simulator.kvm.KVMSimulatorConfig;
import org.zstack.simulator.virtualrouter.VirtualRouterSimulatorConfig;
import org.zstack.test.*;
import org.zstack.test.deployer.Deployer;
import org.zstack.utils.CollectionUtils;
import org.zstack.utils.Utils;
import org.zstack.utils.data.SizeUnit;
import org.zstack.utils.function.Function;
import org.zstack.utils.logging.CLogger;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestKvmHostCapacityOnFailure {
    CLogger logger = Utils.getLogger(TestKvmHostCapacityOnFailure.class);

	Deployer deployer;
	Api api;
	ComponentLoader loader;
	CloudBus bus;
	DatabaseFacade dbf;
	SessionInventory session;
	VirtualRouterSimulatorConfig vconfig;
	KVMSimulatorConfig kconfig;
    ThreadFacade thdf;
    int total = 100;
    int syncLevel = 100;
    int timeout = 1200;

	@Before
	public void setUp() throws Exception {
		DBUtil.reDeployDB();
		WebBeanConstructor con = new WebBeanConstructor();
		deployer = new Deployer("deployerXml/kvm/TestKvm1000Vm.xml", con);
		deployer.addSpringConfig("VirtualRouter.xml");
		deployer.addSpringConfig("VirtualRouterSimulator.xml");
		deployer.addSpringConfig("KVMRelated.xml");

        ThreadGlobalProperty.MAX_THREAD_NUM = 500;
        CoreGlobalProperty.VM_TRACER_ON = false;

		deployer.build();
		api = deployer.getApi();
		loader = deployer.getComponentLoader();
		vconfig = loader.getComponent(VirtualRouterSimulatorConfig.class);
		kconfig = loader.getComponent(KVMSimulatorConfig.class);
		bus = loader.getComponent(CloudBus.class);
		dbf = loader.getComponent(DatabaseFacade.class);
        thdf = loader.getComponent(ThreadFacade.class);
		session = api.loginAsAdmin();
	}

	@Test
	public void test() throws ApiSenderException, InterruptedException {
        kconfig.startVmFailureChance = 0.8;
        HostInventory host = deployer.hosts.get("host1");
        HostCapacityVO hcap = dbf.findByUuid(host.getUuid(), HostCapacityVO.class);

        L2NetworkInventory l2 = deployer.l2Networks.get("TestL2Network");
        SimpleQuery<L3NetworkVO> l3q = dbf.createQuery(L3NetworkVO.class);
        l3q.add(L3NetworkVO_.l2NetworkUuid, Op.EQ, l2.getUuid());
        List<L3NetworkVO> l3vos = l3q.list();
        final List<String> l3Uuids = CollectionUtils.transformToList(l3vos, new Function<String, L3NetworkVO>() {
            @Override
            public String call(L3NetworkVO arg) {
                return arg.getUuid();
            }
        });

        final ImageInventory img = deployer.images.get("TestImage");
        ImageVO imgvo = dbf.findByUuid(img.getUuid(), ImageVO.class);
        imgvo.setSize(1);
        dbf.update(imgvo);
        final InstanceOfferingInventory ioinv = deployer.instanceOfferings.get("TestInstanceOffering");
        final Random random = new Random();

        final CountDownLatch latch = new CountDownLatch(total);
        for (int i=0; i<total; i++) {
            final int finalI = i;
            thdf.syncSubmit(new SyncTask<Object>() {
                @Override
                public String getSyncSignature() {
                    return "creating-vm";
                }

                @Override
                public int getSyncLevel() {
                    return syncLevel;
                }

                @Override
                public String getName() {
                    return getSyncSignature();
                }

                @Override
                public Object call() throws Exception {
                    try {
                        VmCreator creator = new VmCreator(api);
                        creator.addL3Network(l3Uuids.get(random.nextInt(l3Uuids.size())));
                        creator.instanceOfferingUuid = ioinv.getUuid();
                        creator.imageUuid = img.getUuid();
                        creator.name = "vm-" + finalI;
                        creator.timeout = (int) TimeUnit.MINUTES.toSeconds(10);
                        creator.create();
                    } finally {
                        latch.countDown();
                    }
                    return null;
                }
            });
        }

        latch.await(timeout, TimeUnit.MINUTES);

        List<VmInstanceVO> vms = dbf.listAll(VmInstanceVO.class);
        final CountDownLatch latch1 = new CountDownLatch(vms.size());
        for (final VmInstanceVO vm : vms) {
            new Runnable() {
                @Override
                @AsyncThread
                public void run() {
                    try {
                        api.destroyVmInstance(vm.getUuid());
                    } catch (ApiSenderException e) {
                        logger.warn(e.getMessage(), e);
                    } finally {
                        latch1.countDown();
                    }
                }
            }.run();
        }

        TimeUnit.SECONDS.sleep(5);
        HostCapacityVO hcap1 = dbf.findByUuid(host.getUuid(), HostCapacityVO.class);
        Assert.assertEquals(String.format("mismatch memory capacity: %s", SizeUnit.BYTE.toMegaByte(hcap.getAvailableMemory() - hcap1.getAvailableMemory())),
                hcap.getAvailableMemory(), hcap1.getAvailableMemory());
	}
}
