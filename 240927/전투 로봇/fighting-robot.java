import java.util.*;
import java.io.*;

public class Main {
	
	static class Robot{
		int r;
		int c;
		int level = 2;
		int kill = 0;
		
		public Robot(int r, int c) {
			this.r = r;
			this.c = c;
		}
		
		void levelUp() {
			level++;
			kill = 0;
		}
		
		void kill() {
			if(++kill == level) levelUp();
		}
	}
	
	static class Monster{
		public Monster(int r, int c, int level) {
			this.r = r;
			this.c = c;
			this.level = level;
		}
		
		int r;
		int c;
		int level;
	}
	
	static List<Monster> monsterList = new ArrayList<>();
	static Robot robot;
	static int N;
	static int[] dr = {-1, 0, 0, 1};
	static int[] dc = {0, -1, 1, 0};
	static int[][] map;
	
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		N = Integer.parseInt(br.readLine());
		map = new int[N][N];
		
		for(int i = 0; i < N; i++) {
			StringTokenizer st = new StringTokenizer(br.readLine());
			for(int j = 0; j < N; j++) {
				int now = Integer.parseInt(st.nextToken());
				map[i][j] = now;
				if(now >= 1 && now <= 6) monsterList.add(new Monster(i, j, now));
				if(now == 9) robot = new Robot(i, j);
			}
		}
		
		
		int time = 0;
		while(true) {
			int moveTime = moveRobot();
			if(moveTime == -1) break;
			time += moveTime;
		}
		
		System.out.println(time);
	}
	
	static int moveRobot() {
		boolean[][] visited = new boolean[N][N];
		Queue<int[]> queue = new ArrayDeque<>();
		queue.offer(new int[] {robot.r, robot.c});
		visited[robot.r][robot.c] = true;
		int depth = 0;
		int qSize = 1;
		
		while(!queue.isEmpty()) {
			depth++;
			qSize = queue.size();
			for(int qs = 0; qs < qSize; qs++) {
				int[] nowLoc = queue.poll();
				int r = nowLoc[0];
				int c = nowLoc[1];
				
				for(int d = 0; d < 4; d++) {
					int nr = r + dr[d];
					int nc = c + dc[d];
					
					if(OOB(nr, nc)) continue; //맵 밖으로 나가면 continue
					if(visited[nr][nc]) continue; //이미 지나간 길이면 continue
					if(map[nr][nc] > robot.level) continue; //해당 칸 위에 몬스터 레벨이 더 높으면 continue
					
					if(map[nr][nc] != 0 && map[nr][nc] < robot.level) {
						map[robot.r][robot.c] = 0;
						robot.r = nr;
						robot.c = nc;
						map[nr][nc] = 9;
						robot.kill();
						return depth;
					}
					queue.offer(new int[] {nr, nc});
					visited[nr][nc] = true;
				}
			}
		}
		
		return -1;
	}
	
	static boolean OOB(int r, int c) { return r < 0 || r >= N || c < 0 || c >= N; }
	
	static void printMap() {
		for(int[] m : map) {
			System.out.println(Arrays.toString(m));
		}
	}

}