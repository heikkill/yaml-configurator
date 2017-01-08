package com.heikkill.yaml;

import java.io.InputStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.heikkill.yaml.beans.Bean1;
import com.heikkill.yaml.beans.Bean2;
import com.heikkill.yaml.beans.Bean3;
import com.heikkill.yaml.beans.Bean4;
import com.heikkill.yaml.beans.SubBean;

public class YamlConfigTest {
	
	public static final String PKG_TEST_CLASSES = "com.heikkill.yaml.beans";
	public static final String PKG_CIRC_CLASSES = "com.heikkill.yaml.circular";

	@Test
	public void configToBeansTest() throws Exception {
		ProcessedConfig processed = new ProcessedConfig();
		YamlConfigReader reader = new YamlConfigReader(PKG_TEST_CLASSES);
		InputStream is = getClass().getClassLoader().getResourceAsStream("test.yaml");
		reader.load(is, processed);
		
		Assert.assertEquals(processed.bean1List.size(), 1);
		Assert.assertEquals(processed.bean2List.size(), 2);
		Assert.assertEquals(processed.bean3List.size(), 1);
		Assert.assertEquals(processed.bean4List.size(), 1);
		
		Bean1 bean1 = processed.bean1List.get(0);
		Assert.assertEquals(bean1.getProp1(), "value1");
		Assert.assertEquals(bean1.getProp2(), 2);
		
		Bean2 bean2_1 = processed.bean2List.get(0);
		Assert.assertEquals(bean2_1.getProps().size(), 1);
		Assert.assertEquals(bean2_1.getProps().get(0), "string1");
		Assert.assertEquals(bean2_1.getInjectedProvider() != null, true);
		Assert.assertEquals(bean2_1.getInjectedProvider().get("string5").intValue(), 123);
		Assert.assertEquals(bean2_1.getInjectedProvider().get("string6").intValue(), 456);
		
		Bean2 bean2_2 = processed.bean2List.get(1);
		Assert.assertEquals(bean2_2.getProps().size(), 3);
		Assert.assertEquals(bean2_2.getProps().get(0), "string2");
		Assert.assertEquals(bean2_2.getProps().get(1), "string3");
		Assert.assertEquals(bean2_2.getProps().get(2), "string4");
		
		Bean3 bean3 = processed.bean3List.get(0);
		Assert.assertEquals(bean3.getProps().size(), 2);
		Assert.assertEquals(bean3.getProps().get("string5").intValue(), 123);
		Assert.assertEquals(bean3.getProps().get("string6").intValue(), 456);
		
		Bean4 bean4 = processed.bean4List.get(0);
		Assert.assertEquals(bean4.getProp() != null, true);
		SubBean subBean = bean4.getProp();
		Assert.assertEquals(subBean.getProp(), "string7");
	}
	
	@Test
	public void configToBeansWithoutExtendedProcessedObjectTest() throws Exception {
		YamlConfigReader reader = new YamlConfigReader(PKG_TEST_CLASSES);
		InputStream is = getClass().getClassLoader().getResourceAsStream("test.yaml");
		YamlConfig yamlConfig = reader.load(is);
		Assert.assertEquals(yamlConfig.getProcessed().getAllResults().size(), 5);
	}
	
	
	@Test(expectedExceptions = { YamlNamespaceException.class })
	public void namespaceHandlerMissingTest() throws Exception {
		ProcessedConfig processed = new ProcessedConfig();
		YamlConfigReader reader = new YamlConfigReader(PKG_TEST_CLASSES + "foo");
		InputStream is = getClass().getClassLoader().getResourceAsStream("test.yaml");
		reader.load(is, processed);
	}
	
	@Test(expectedExceptions = { YamlNamespaceException.class }, expectedExceptionsMessageRegExp =
			"Circular dependency detected\\. Chain\\: \\[root1\\.prop1, circ2, circ3, root1\\.prop1\\]")
	public void circularDependencyChainTest() throws Exception {
		ProcessedConfig processed = new ProcessedConfig();
		YamlConfigReader reader = new YamlConfigReader(PKG_CIRC_CLASSES);
		InputStream is = getClass().getClassLoader().getResourceAsStream("test.yaml");
		reader.load(is, processed);
	}
}
