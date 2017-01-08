package com.heikkill.yaml;

import java.util.ArrayList;
import java.util.List;

import com.heikkill.yaml.beans.Bean1;
import com.heikkill.yaml.beans.Bean2;
import com.heikkill.yaml.beans.Bean3;
import com.heikkill.yaml.beans.Bean4;

public class ProcessedConfig extends YamlConfig.Processed {
	
	public List<Bean1> bean1List = new ArrayList<>();
	public List<Bean2> bean2List = new ArrayList<>();
	public List<Bean3> bean3List = new ArrayList<>();
	public List<Bean4> bean4List = new ArrayList<>();
	
	@Override
	public void handleResult(Object result) {
		super.handleResult(result);
		
		if (result instanceof Bean1) {
			bean1List.add((Bean1)result);
		}
		else if (result instanceof Bean2) {
			bean2List.add((Bean2)result);
		}
		else if (result instanceof Bean3) {
			bean3List.add((Bean3)result);
		}
		else if (result instanceof Bean4) {
			bean4List.add((Bean4)result);
		}
	}
}
