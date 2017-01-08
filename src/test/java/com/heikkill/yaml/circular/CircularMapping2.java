package com.heikkill.yaml.circular;

import java.util.Arrays;
import java.util.List;

import com.heikkill.yaml.YamlNamespaceMapping;

public class CircularMapping2 implements YamlNamespaceMapping {

	public static final String NAMESPACE = "circ2";

	@Override
	public Class<?> getProducedClass() {
		return CircularMapping2.class;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public List<String> getNamespaceDependencies() {
		return Arrays.asList(CircularMapping3.NAMESPACE);
	}
}
