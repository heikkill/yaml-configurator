## yaml-configurator
YAML config to Java beans 

## Overview
Yaml-configurator uses the [Jackson YAML extension](https://github.com/FasterXML/jackson-dataformat-yaml) to deserialize data from a YAML configuration file and handles dependencies between different parts of the configuration file. During the processing of a config file, beans already deserialized can provide data to other dependent beans.

## Usage
### Namespace mappings
Implement your beans representing parts of your YAML config file and provide a **YamlNamespaceMapping** annotation for each bean. The mapping defines the namespace of the config file to handle and dependencies to other namespaces. The namespace corresponds to the structure of the YAML file using a dot as a delimiter.

**Java:**
```java
@YamlNamespaceMapping(namespace = Bean1.NAMESPACE, namespaceDependencies = {})
public class Bean1 {
	public static final String NAMESPACE = "root1.sub1";
	
	private String prop1;
	private int prop2;
}
```
**Yaml:**
```yaml
root1:
  sub1:
    prop1: value1
    prop2: 123
```

### Namespace dependencies & passing data from a bean to another
Data can be provided from one bean to another during the deserialization of a config file.

A bean that expects data from other beans needs to define a list of namespace dependencies and a **Provider** field annotated with **@JacksonInject**:
```java
@YamlNamespaceMapping(namespace = Bean2.NAMESPACE, namespaceDependencies = { Bean3.NAMESPACE })
public class Bean2 {
	public static final String NAMESPACE = "root2";
	
	@JacksonInject(value = "example")
	private Provider<Integer> intProvider;
}
```

A bean that provides data to other beans has to implement **ProducesProviders**
```java
public class Bean3 implements ProducesProviders {
  private Map<String, Integer> props; 
	
  @Override
	public Map<String, Provider<?>> getProviders() {
		Map<String, Provider<?>> map = new HashMap<String, Provider<?>>();
		map.put("example", new Provider<Integer>() {
			@Override
			public Integer get(String name) {
				return props.get(name);
			}
		});
		return map;
	}
}
```

### Raw & Processed configuration
A **YamlConfig** object produced by **YamlConfigReader** contains both the raw config data (basically maps, lists & strings deserialized by Jackson) and the processed config data i.e. the beans. **YamlConfig.Processed** class can be extended to suit specific needs, f.ex:
```java
public class ProcessedConfig extends YamlConfig.Processed {
	public List<Bean1> bean1List = new ArrayList<>();
	
	@Override
	public void handleResult(Object result) {
		super.handleResult(result);
		
		if (result instanceof Bean1) {
			bean1List.add((Bean1)result);
		}
	}
}
```
Also worth noting is that discovered Provider objects are saved to and then later fetched from the YamlConfig.Processed object. This means that an extended YamlConfig.Processed object could do neat tricks such as combine data from various beans or other sources and then make the data available as Providers.

## Example
With a proper bean structure in place, simply instantiate a reader and read data from a file, string or input stream:
```java
YamlConfigReader reader = new YamlConfigReader("my.package.of.beans.to.scan");
File file = new File("myconfig.yaml");
YamlConfig yamlConfig = reader.load(file);
```
