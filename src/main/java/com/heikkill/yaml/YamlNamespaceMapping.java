package com.heikkill.yaml;

import java.util.List;


public interface YamlNamespaceMapping {

	Class<?> getProducedClass();
	String getNamespace();
	List<String> getNamespaceDependencies();
	
}
