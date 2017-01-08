package com.heikkill.yaml.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.heikkill.yaml.ProducesProviders;
import com.heikkill.yaml.Provider;
import com.heikkill.yaml.YamlNamespaceMapping;

public class Bean3 implements ProducesProviders {
	
	public static final String STUFF = "stuff";
	
	private Map<String, Integer> props; 
	
	@Override
	public Map<String, Provider<?>> getProviders() {
		Map<String, Provider<?>> map = new HashMap<String, Provider<?>>();
		map.put(STUFF, new Provider<Integer>() {
			@Override
			public Integer get(String name) {
				return props.get(name);
			}
		});
		return map;
	}

	public Map<String, Integer> getProps() {
		return props;
	}

	public void setProps(Map<String, Integer> props) {
		this.props = props;
	}

	public static class Mapping implements YamlNamespaceMapping {
		
		public static final String NAMESPACE = "root3.sub1.sub2";

		@Override
		public Class<?> getProducedClass() {
			return Bean3.class;
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
