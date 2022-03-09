package tech.jaboc.jcom.mission.message;

import tech.jaboc.jcom.mission.common.Map;

import java.io.*;

public class MapSynchronizeAction extends Message {
	public Map map;
	
	public MapSynchronizeAction(Map map) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = null;
			oos = new ObjectOutputStream(baos);
			oos.writeObject(map);
			
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			this.map = (Map) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
