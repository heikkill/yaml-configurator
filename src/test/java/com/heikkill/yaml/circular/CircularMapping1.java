package com.heikkill.yaml.circular;

import java.util.Arrays;
import java.util.List;

import com.heikkill.yaml.YamlNamespaceMapping;

public class CircularMapping1 implements YamlNamespaceMapping {

	public static final String NAMESPACE = "root1.prop1";

	@Override
	public Class<?> getProducedClass() {
		return CircularMapping1.class;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public List<String> getNamespaceDependencies() {
		return Arrays.asList(CircularMapping2.NAMESPACE);
	}
}
