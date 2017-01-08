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
import com.heikkill.yaml.YamlConfig.Processed;

public class YamlConfigReader {
	
	private ObjectMapper om = new ObjectMapper(new YAMLFactory());
	private Map<String, YamlNamespaceMapping> namespaceMappings;
	private List<String> handledNamespaces = new ArrayList<String>();
	
	public YamlConfigReader(Map<String, YamlNamespaceMapping> namespaceMappings) {
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

	private Map<String, YamlNamespaceMapping> acquireNamespaceMappings(Reflections reflections) {
		List<YamlNamespaceMapping> list = acquireInstances(reflections, YamlNamespaceMapping.class);
		Map<String, YamlNamespaceMapping> map = new HashMap<String, YamlNamespaceMapping>();
		for (YamlNamespaceMapping hb : list) {
			map.put(hb.getNamespace(), hb);
		}
		return map;
	}

	private void handleNamespace(YamlConfig config, String namespace, String...namespaceDependencyChain) throws YamlNamespaceException {
		if (handledNamespaces.contains(namespace)) {
			return;
		}
		
		if (namespaceMappings.containsKey(namespace)) {
			YamlNamespaceMapping mapping = namespaceMappings.get(namespace);
			if (mapping.getNamespaceDependencies() != null) {
				for (String dependency : mapping.getNamespaceDependencies()) {
					String[] newChain = createDependencyChain(namespaceDependencyChain, namespace);
					handleNamespace(config, dependency, newChain);
				}
			}
			
			Object result = produce(config.getRaw().get(namespace),  namespaceMappings.get(namespace).getProducedClass());
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
	
	private <E> List<E> acquireInstances(Reflections reflections, Class<E> clazz) {
		Set<Class<? extends E>> classes = reflections.getSubTypesOf(clazz);
		List<E> list = new ArrayList<E>();
		for (Class<? extends E> c : classes) {
			try {
				list.add(c.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}
