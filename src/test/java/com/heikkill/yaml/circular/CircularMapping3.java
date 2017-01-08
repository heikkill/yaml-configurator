package com.heikkill.yaml.circular;

import java.util.Arrays;
import java.util.List;

import com.heikkill.yaml.YamlNamespaceMapping;

public class CircularMapping3 implements YamlNamespaceMapping {

	public static final String NAMESPACE = "circ3";

	@Override
	public Class<?> getProducedClass() {
		return CircularMapping3.class;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public List<String> getNamespaceDependencies() {
		return Arrays.asList(CircularMapping1.NAMESPACE);
	}
}
