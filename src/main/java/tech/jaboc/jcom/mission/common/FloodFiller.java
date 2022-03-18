package tech.jaboc.jcom.mission.common;

import java.util.*;

public class FloodFiller {
	public static FloodFillResult floodFill(Map map, Tile startingTile, int depth) {
//		long starts = System.nanoTime();
		
		PriorityQueue<FloodFillTile> queue = new PriorityQueue<>(Comparator.comparingInt(o -> o.priority));
		HashMap<FloodFillTile, Integer> dist = new HashMap<>();
		HashMap<FloodFillTile, FloodFillTile> prev = new HashMap<>();
		
		HashSet<FloodFillTile> accessibleTiles = new HashSet<>();
		
		FloodFillTile tile = new FloodFillTile(startingTile.x, startingTile.y, 0);
		prev.put(tile, null);
		dist.put(tile, 0);
		queue.add(tile);
		
		while (queue.size() != 0) {
			FloodFillTile current = queue.remove();
			if (current.priority > depth) continue;
			accessibleTiles.add(current);
			for (FloodFillTile t : current.getNeighbors(map)) {
				if (t == null) continue;
				if (!dist.containsKey(t)) {
					dist.put(t, t.priority);
					prev.put(t, current);
					queue.add(t);
				} else if (dist.get(t) > t.priority) {
					queue.remove(t);
					dist.put(t, t.priority);
					prev.put(t, current);
					queue.add(t);
				}
			}
		}
		
		FloodFillResult result = new FloodFillResult();
		result.dist = dist;
		result.prev = prev;
		result.accessibleTiles = accessibleTiles;


//		long ends = System.nanoTime();
//		System.out.println(ends - starts);
		
		return result;
	}
	
	public static class FloodFillResult {
		public HashSet<FloodFillTile> accessibleTiles;
		public HashMap<FloodFillTile, FloodFillTile> prev;
		public HashMap<FloodFillTile, Integer> dist;
	}
	
	public static class FloodFillTile {
		public int x, y;
		public int priority;
		
		public FloodFillTile(int x, int y, int priority) {
			this.x = x;
			this.y = y;
			this.priority = priority;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			FloodFillTile that = (FloodFillTile) o;
			return x == that.x && y == that.y;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(x, y);
		}
		
		public FloodFillTile[] getNeighbors(Map map) {
			FloodFillTile[] tiles = new FloodFillTile[4];
			if (x > 0 && map.tiles[x - 1][y].passable) {
				tiles[0] = new FloodFillTile(x - 1, y, priority + 1);
			}
			if (x < map.getLength() - 1 && map.tiles[x + 1][y].passable) {
				tiles[1] = new FloodFillTile(x + 1, y, priority + 1);
			}
			if (y > 0 && map.tiles[x][y - 1].passable) {
				tiles[2] = new FloodFillTile(x, y - 1, priority + 1);
			}
			if (y < map.getWidth() - 1 && map.tiles[x][y + 1].passable) {
				tiles[3] = new FloodFillTile(x, y + 1, priority + 1);
			}
			return tiles;
		}
	}
}
