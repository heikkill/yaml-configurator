package com.heikkill.yaml.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.heikkill.yaml.Annotations.YamlNamespaceMapping;
import com.heikkill.yaml.Provider;

@YamlNamespaceMapping(namespace = Bean2.NAMESPACE, namespaceDependencies = { Bean3.NAMESPACE })
public class Bean2 {
	
	public static final String NAMESPACE = "root2";
	
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
}
