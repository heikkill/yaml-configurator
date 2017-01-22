package com.heikkill.yaml.beans;

import java.util.HashMap;
import java.util.Map;

import com.heikkill.yaml.Annotations.YamlNamespaceMapping;
import com.heikkill.yaml.ProducesProviders;
import com.heikkill.yaml.Provider;

@YamlNamespaceMapping(namespace = Bean3.NAMESPACE)
public class Bean3 implements ProducesProviders {
	
	public static final String NAMESPACE = "root3.sub1.sub2";
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
}
