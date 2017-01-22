package com.heikkill.yaml.circular;

import com.heikkill.yaml.Annotations.YamlNamespaceMapping;

@YamlNamespaceMapping(namespace = CircularMapping2.NAMESPACE, namespaceDependencies = { CircularMapping3.NAMESPACE })
public class CircularMapping2 {

	public static final String NAMESPACE = "circ2";
	
}
