package com.heikkill.yaml.beans;

import com.heikkill.yaml.Annotations.YamlNamespaceMapping;

@YamlNamespaceMapping(namespace = Bean4.NAMESPACE)
public class Bean4 {
	
	public static final String NAMESPACE = "root3.sub3";
	
	private SubBean prop;

	public SubBean getProp() {
		return prop;
	}

	public void setProp(SubBean prop) {
		this.prop = prop;
	}
}
