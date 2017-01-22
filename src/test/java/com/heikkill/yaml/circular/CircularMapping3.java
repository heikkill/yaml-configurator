package com.heikkill.yaml.circular;

import com.heikkill.yaml.Annotations.YamlNamespaceMapping;

@YamlNamespaceMapping(namespace = CircularMapping3.NAMESPACE, namespaceDependencies = { CircularMapping1.NAMESPACE })
public class CircularMapping3 {

	public static final String NAMESPACE = "circ3";
	
}
