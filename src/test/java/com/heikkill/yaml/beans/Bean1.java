package com.heikkill.yaml.beans;

import java.util.List;

import com.heikkill.yaml.YamlNamespaceMapping;

public class Bean1 {
	
	private String prop1;
	private int prop2;

	public String getProp1() {
		return prop1;
	}

	public void setProp1(String prop1) {
		this.prop1 = prop1;
	}

	public int getProp2() {
		return prop2;
	}

	public void setProp2(int prop2) {
		this.prop2 = prop2;
	}

	public static class Mapping implements YamlNamespaceMapping {
		
		public static final String NAMESPACE = "root1";

		@Override
		public Class<?> getProducedClass() {
			return Bean1.class;
		}

		@Override
		public String getNamespace() {
			return NAMESPACE;
		}

		@Override
		public List<String> getNamespaceDependencies() {
			return null;
		}
	}
}
