package me.pugabyte.bncore.framework.persistence.serializer;

import com.dieselpoint.norm.serialize.DbSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IntegerListSerializer implements DbSerializable {
	@Override
	public String serialize(Object in) {
		return ((List<Integer>) in).stream().map(String::valueOf).collect(Collectors.joining(","));
	}

	@Override
	public List<Integer> deserialize(String in) {
		List<Integer> ints = new ArrayList<>();
		Arrays.asList(in.split(",")).forEach(string -> ints.add(Integer.parseInt(string)));
		return ints;
	}
}
