package com.heikkill.yaml;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.heikkill.yaml.Annotations.YamlNamespaceMapping;
import com.heikkill.yaml.YamlConfig.Processed;

public class YamlConfigReader {
	
	private ObjectMapper om = new ObjectMapper(new YAMLFactory());
	private Map<String, Mapping> namespaceMappings;
	private List<String> handledNamespaces = new ArrayList<String>();
	
	public YamlConfigReader(Map<String, Mapping> namespaceMappings) {
		this.namespaceMappings = namespaceMappings;
	}
	
	public YamlConfigReader() {
		this(new Reflections());
	}
	
	public YamlConfigReader(String namespaceMappingPackage) {
		this(new Reflections(namespaceMappingPackage));
	}
	
	public YamlConfigReader(Reflections reflections) {
		namespaceMappings = acquireNamespaceMappings(reflections);
	}
	
	public YamlConfig load(InputStream is) throws Exception {
		return load(is, new Processed(true));
	}
	
	public YamlConfig load(String configStr) throws Exception {
		return load(configStr, new Processed(true));
	}
	
	public YamlConfig load(File configFile) throws Exception {
		return load(configFile, new Processed(true));
	}
	
	public YamlConfig load(InputStream is, Processed processed) throws Exception {
		YamlConfig.Raw raw = om.readValue(is, YamlConfig.Raw.class);
		return processConfig(raw, processed);
	}
	
	public YamlConfig load(String configStr, Processed processed) throws Exception {
		YamlConfig.Raw raw = om.readValue(configStr, YamlConfig.Raw.class);
		return processConfig(raw, processed);
	}
	
	public YamlConfig load(File configFile, Processed processed) throws Exception {
		YamlConfig.Raw raw = om.readValue(configFile, YamlConfig.Raw.class);
		return processConfig(raw, processed);
	}
	
	public YamlConfig processConfig(YamlConfig.Raw raw, YamlConfig.Processed processed) throws Exception {
		return processConfig(new YamlConfig(raw, processed));
	}
	
	public YamlConfig processConfig(YamlConfig config) throws Exception {
		enableJacksonInjection(config, om);
		
		for (String key : config.getRaw().getRootNamespaces()) {
			handleNamespace(config, key);
		}
		return config;
	}

	private Map<String, Mapping> acquireNamespaceMappings(Reflections reflections) {
		Map<String, Mapping> map = new HashMap<String, Mapping>();
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Annotations.YamlNamespaceMapping.class);
		for (Class<?> c : classes) {
			Mapping mapping = new Mapping(c);
			map.put(mapping.namespace, mapping);
		}
		return map;
	}

	private void handleNamespace(YamlConfig config, String namespace, String...namespaceDependencyChain) throws YamlNamespaceException {
		if (handledNamespaces.contains(namespace)) {
			return;
		}
		
		if (namespaceMappings.containsKey(namespace)) {
			Mapping mapping = namespaceMappings.get(namespace);
			if (mapping.namespaceDependencies != null) {
				for (String dependency : mapping.namespaceDependencies) {
					String[] newChain = createDependencyChain(namespaceDependencyChain, namespace);
					handleNamespace(config, dependency, newChain);
				}
			}
			
			Object result = produce(config.getRaw().get(namespace),  namespaceMappings.get(namespace).producedClass);
			config.getProcessed().handleResult(result);
			handledNamespaces.add(namespace);
		}
		else {
			Object object = config.getRaw().get(namespace);
			if (object instanceof Map<?, ?>) {
				@SuppressWarnings("unchecked")
				Map<String, ?> map = (Map<String, ?>)object;
				for (String key : map.keySet()) {
					handleNamespace(config, namespace + "." + key);
				}
			}
			else {
				throw new YamlNamespaceException("No handler for namespace " + namespace);
			}
		}
	}

	private String[] createDependencyChain(String[] chain, String newNamespace) throws YamlNamespaceException {
		String[] newChain = Arrays.copyOf(chain, chain.length + 1);
		newChain[newChain.length - 1] = newNamespace;
		for (String s : chain) {
			if (s.equals(newNamespace)) {
				throw new YamlNamespaceException("Circular dependency detected. Chain: " + Arrays.toString(newChain));
			}
		}
		
		return newChain;
	}

	private Object produce(Object object, Class<?> clazz) {
		if (object instanceof List) {
			List<?> list = (List<?>) object;
			List<Object> results = new ArrayList<Object>();
			for (Object obj : list) {
				results.add(om.convertValue(obj, clazz));
			}
			return results;
		}
		else {
			return om.convertValue(object, clazz);
		}
	}
	
	private void enableJacksonInjection(final YamlConfig config, ObjectMapper om) {
		InjectableValues injectable = new InjectableValues() {
			@Override
			public Object findInjectableValue(Object valueId, DeserializationContext ctxt, BeanProperty forProperty, Object beanInstance) {
				return config.getProcessed().getProvider(valueId);
			}
		};
		om.setInjectableValues(injectable);
	}
	
	private class Mapping {
		Class<?> producedClass;
		String namespace;
		List<String> namespaceDependencies;
		
		public Mapping(Class<?> producedClass) {
			this.producedClass = producedClass;
			YamlNamespaceMapping yamlNamespaceMapping = producedClass.getAnnotation(Annotations.YamlNamespaceMapping.class);
			this.namespace = yamlNamespaceMapping.namespace();
			this.namespaceDependencies = Arrays.asList(yamlNamespaceMapping.namespaceDependencies());
		}
	}
}
