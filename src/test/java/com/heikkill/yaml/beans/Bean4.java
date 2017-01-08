package com.heikkill.yaml.beans;

import java.util.List;

import com.heikkill.yaml.YamlNamespaceMapping;

public class Bean4 {
	
	private SubBean prop;

	public SubBean getProp() {
		return prop;
	}

	public void setProp(SubBean prop) {
		this.prop = prop;
	}

	public static class Mapping implements YamlNamespaceMapping {
		
		public static final String NAMESPACE = "root3.sub3";

		@Override
		public Class<?> getProducedClass() {
			return Bean4.class;
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
