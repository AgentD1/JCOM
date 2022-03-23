package tech.jaboc.jcom.mission.common;

import java.util.*;

public class FloodFiller {
	public static FloodFillResult floodFill(Map map, Tile startingTile, int depth) {
		return floodFill(map, startingTile, depth, EnumSet.noneOf(FloodFillOptions.class));
	}
	
	public static FloodFillResult floodFill(Map map, Tile startingTile, int depth, EnumSet<FloodFillOptions> options) {
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
			for (FloodFillTile t : current.getNeighbors(map, options)) {
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
		
		if (!options.contains(FloodFillOptions.ALLOW_OCCUPIED)) {
			List<FloodFillTile> evilOnes = new ArrayList<>();
			for (FloodFillTile t : accessibleTiles) {
				if (map.units.stream().anyMatch(u -> u.x == t.x && u.y == t.y)) {
					evilOnes.add(t);
				}
			}
			for (FloodFillTile t : evilOnes) {
				accessibleTiles.remove(t);
			}
		}
		
		FloodFillResult result = new FloodFillResult();
		result.dist = dist;
		result.prev = prev;
		result.accessibleTiles = accessibleTiles;
		result.optionsUsed = options;


//		long ends = System.nanoTime();
//		System.out.println(ends - starts);
		
		return result;
	}
	
	public static class FloodFillResult {
		public HashSet<FloodFillTile> accessibleTiles;
		public HashMap<FloodFillTile, FloodFillTile> prev;
		public HashMap<FloodFillTile, Integer> dist;
		public EnumSet<FloodFillOptions> optionsUsed;
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
		
		public FloodFillTile[] getNeighbors(Map map, EnumSet<FloodFillOptions> options) {
			FloodFillTile[] tiles = new FloodFillTile[4];
			if (x > 0 && (!options.contains(FloodFillOptions.ALLOW_IMPASSIBLE) && map.tiles[x - 1][y].passable)) {
				if (options.contains(FloodFillOptions.ALLOW_PASSAGE_THROUGH_OCCUPIED) || map.units.stream().noneMatch(u -> u.x == x && u.y == y)) {
					tiles[0] = new FloodFillTile(x - 1, y, priority + 1);
				}
			}
			if (x < map.getLength() - 1 && (!options.contains(FloodFillOptions.ALLOW_IMPASSIBLE) && map.tiles[x + 1][y].passable)) {
				if (options.contains(FloodFillOptions.ALLOW_PASSAGE_THROUGH_OCCUPIED) || map.units.stream().noneMatch(u -> u.x == x && u.y == y)) {
					tiles[1] = new FloodFillTile(x + 1, y, priority + 1);
				}
			}
			if (y > 0 && (!options.contains(FloodFillOptions.ALLOW_IMPASSIBLE) && map.tiles[x][y - 1].passable)) {
				if (options.contains(FloodFillOptions.ALLOW_PASSAGE_THROUGH_OCCUPIED) || map.units.stream().noneMatch(u -> u.x == x && u.y == y)) {
					tiles[2] = new FloodFillTile(x, y - 1, priority + 1);
				}
			}
			if (y < map.getWidth() - 1 && (!options.contains(FloodFillOptions.ALLOW_IMPASSIBLE) && map.tiles[x][y + 1].passable)) {
				if (options.contains(FloodFillOptions.ALLOW_PASSAGE_THROUGH_OCCUPIED) || map.units.stream().noneMatch(u -> u.x == x && u.y == y)) {
					tiles[3] = new FloodFillTile(x, y + 1, priority + 1);
				}
			}
			return tiles;
		}
	}
	
	public enum FloodFillOptions {
		ALLOW_IMPASSIBLE,   // Allow impassible tiles?
		ALLOW_OCCUPIED,     // Allow occupied tiles? (by units)
		ALLOW_PASSAGE_THROUGH_OCCUPIED // Allow passage through occupied tiles (but not to end on one)?
	}
}
