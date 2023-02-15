package com.xw.glue.context;

import java.util.HashMap;
import java.util.Map;

public class JGlueMap2<V> {
	@SuppressWarnings({"unchecked"})
	Entry<V>[] eArray = (Entry<V>[])new Entry[16];
	
	public JGlueMap2() {
		
	}
	
	public V put(String key, V value) {
		int index = (eArray.length - 1) & hash(key);
		Entry<V> entry = eArray[index];
		if(entry==null) {
			eArray[index] = new Entry<V>(key, value);
		} else {
			entry.put(key, value);
		}
		return value;
	}
	
	public V get(String key) {
		int index = (eArray.length - 1) & hash(key);
		Entry<V> entry = eArray[index];
		if(entry==null) {
			return null;
		}
		return entry.get(key);
	}
	
	public boolean containsKey(String key) {
		int index = (eArray.length - 1) & hash(key);
		Entry<V> entry = eArray[index];
		return entry!=null && entry.get(key)!=null;
	}
	
	public class Entry<O> {
		String key;
		O value;
		Entry<O> next;
		
		Entry(String key, O value) {
			this.key = key;
			this.value = value;
		}
		
		public void put(String key, O value) {
			if (key.equals(this.key)) {
				this.value = value;
			} else if(next!=null){
				next.put(key, value);
			} else {
				next = new Entry<O>(key,value);
			}
		}
		
		public O get(String key) {
			if(this.key.equals(key)) {
				return value;
			} else if(next!=null){
				return next.get(key);
			} else {
				return null;
			}
		}
		
		@SuppressWarnings("unchecked")
		public Entry<O> remove(String key) {
			if(this.key.equals(key)) {
				if(next==null) {
					eArray[(eArray.length - 1) & hash(key)] = null;
				} else {
					eArray[(eArray.length - 1) & hash(key)] = (Entry<V>) next;
				}
				return this;
			} else {
				return removeNext(key, this);
			}
		}
		
		private Entry<O> removeNext(String key, Entry<O> pre) {
			if(this.key.equals(key)) {
				pre.next = next;
				return this;
			} else {
				if(next==null) {
					return null;
				} else {
					return next.removeNext(key, this);
				}
			}
		}
	}
	
	static final int hash(String key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
	
	public static void main(String[] args) {
		Map<String, JValue> map = new HashMap<String, JValue>();
		//JGlueMap<JValue> map = new JGlueMap<JValue>();
		long start = System.nanoTime();
		for(int i=0;i<100;i++) {
			map.put("abc", new JValue("abc", 123));
			map.put("def", new JValue("def", 456));
			map.put("ghi", new JValue("ghi", 789));
			
			System.out.println(map.get("abc").value);
			System.out.println(map.get("def").value);
			System.out.println(map.get("ghi").value);
		}
		long end = System.nanoTime();
		System.out.println("耗时："+(end-start)/1000000+"ms");
	}
}
