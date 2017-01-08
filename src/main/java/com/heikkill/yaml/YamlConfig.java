package com.heikkill.yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class YamlConfig {
	
	private final Raw raw;
	private final Processed processed;
	
	public YamlConfig(Raw raw, Processed processed) {
		this.raw = raw;
		this.processed = processed;
	}

	public Raw getRaw() {
		return raw;
	}

	public Processed getProcessed() {
		return processed;
	}
	
	public static class Raw {
		
		private Map<String, Object> rawConfigData = new HashMap<String, Object>();
		
		@JsonAnySetter
		public void setRawData(String name, Object data) {
			rawConfigData.put(name, data);
		}
		
		public Set<String> getRootNamespaces() {
			return rawConfigData.keySet();
		}
		
		public Object get(String namespace) throws YamlNamespaceException {
			if (!namespace.contains(".")) {
				return rawConfigData.get(namespace);
			}
			
			String[] split = namespace.split("\\.");
			Object subObject = rawConfigData;
			for (String s : split) {
				if (subObject instanceof Map) {
					subObject = ((Map<?, ?>) subObject).get(s);
					continue;
				}
				
				throw new YamlNamespaceException("Namespace " + namespace + " not found in config");
			}
			return subObject;
		}
	}
	
	
	public static class Processed {
		
		protected Map<String, Provider<?>> providers = new HashMap<String, Provider<?>>();
		
		public void handleResult(Object result) {
			if (result instanceof List) {
				List<?> list = (List<?>) result;
				for (Object item : list) {
					handleResult(item);
				}
			}
			else if (result instanceof ProducesProviders) {
				providers.putAll(((ProducesProviders)result).getProviders());
			}
		}
		
		public Provider<?> getProvider(Object valueId) {
			return providers.get(valueId);
		}
	}

}
