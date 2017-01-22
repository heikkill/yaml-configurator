package com.heikkill.yaml.circular;

import com.heikkill.yaml.Annotations.YamlNamespaceMapping;

@YamlNamespaceMapping(namespace = CircularMapping1.NAMESPACE, namespaceDependencies = { CircularMapping2.NAMESPACE })
public class CircularMapping1 {

	public static final String NAMESPACE = "root1.prop1";
	
}
