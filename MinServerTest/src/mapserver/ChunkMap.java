package mapserver;

public class ChunkMap {

	private Chunk[][] serverMap = new Chunk[MapServer.MAPSIZE][MapServer.MAPSIZE];
	
	public Chunk getChunk(int x, int y) {
		return serverMap[y][x];
	}

	public void setChunk(int x, int y, Chunk chunk) {
		serverMap[y][x] = chunk;
		
	}
	
	public void drawChunkMap() {
		for (Chunk[] row : serverMap) {
			System.out.println();
			for (Chunk el : row) {
				System.out.print(el==null?" - ":" + ");
			}
		}
	}
}
