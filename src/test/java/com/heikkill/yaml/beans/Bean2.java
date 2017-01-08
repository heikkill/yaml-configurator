package com.heikkill.yaml.beans;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.heikkill.yaml.Provider;
import com.heikkill.yaml.YamlNamespaceMapping;

public class Bean2 {
	
	@JacksonInject(value = Bean3.STUFF)
	private Provider<Integer> injectedProvider;
	
	private List<String> props;
	
	public List<String> getProps() {
		return props;
	}

	public void setProps(List<String> props) {
		this.props = props;
	}
	
	public Provider<Integer> getInjectedProvider() {
		return injectedProvider;
	}

	public static class Mapping implements YamlNamespaceMapping {
		
		public static final String NAMESPACE = "root2";

		@Override
		public Class<?> getProducedClass() {
			return Bean2.class;
		}

		@Override
		public String getNamespace() {
			return NAMESPACE;
		}

		@Override
		public List<String> getNamespaceDependencies() {
			return Arrays.asList(Bean3.Mapping.NAMESPACE);
		}
	}
}
