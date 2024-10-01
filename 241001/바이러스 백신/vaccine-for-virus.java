import java.util.*;
import java.io.*;

public class Main {	
	static int N, M, birusTotalCnt, hospitalCnt;
	static int answer = Integer.MAX_VALUE;
	static int[] dr = {-1, 0, 1, 0};
	static int[] dc = {0, 1, 0, -1};
	static int[] pick;
	static int[][] map;
	static boolean[][] visited;
	static List<int[]> hospitalList = new ArrayList<>();
	static Queue<int[]> queue = new ArrayDeque<>();
	
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		map = new int[N][N];
		visited = new boolean[N][N];
		
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
				if(map[i][j] == 0) birusTotalCnt++;
				if(map[i][j] == 2) hospitalList.add(new int[] {i, j});
			}
		}
		hospitalCnt = hospitalList.size();
		
		pick = new int[M];
		
		pickHospital(0, 0);
		if(answer == Integer.MAX_VALUE) answer = -1;
		System.out.println(answer);
	}
	
	static void pickHospital(int idx, int cnt) {
		if(idx == M) {
			answer = Math.min(answer, bfs());
			return;
		}
		
		for(int i = cnt; i < hospitalCnt; i++) {
			pick[idx] = i;
			pickHospital(idx + 1, i + 1);
		}
		
	}
	
	static int bfs() {
		for(boolean[] v : visited) Arrays.fill(v, false);
		queue.clear();
		
		//queue에 병원 시작점들 넣어주기
		int now[];
		for(int idx : pick) {
			now = hospitalList.get(idx);
			queue.offer(now);
			visited[now[0]][now[1]] = true;
		}
		
		int depth = 0;
		int birusCnt = 0;
		
		while(!queue.isEmpty()) {
			int qSize = queue.size();
			depth++;
			for(int qs = 0; qs < qSize; qs++) {
				now = queue.poll();
				for(int d = 0; d < 4; d++) {
					int nr = now[0] + dr[d];
					int nc = now[1] + dc[d];
					
					if(OOB(nr, nc)) continue;
					if(visited[nr][nc]) continue;
					if(map[nr][nc] != 0) continue;
					
					queue.offer(new int[] {nr, nc});
					visited[nr][nc] = true;
					birusCnt++;
				}
			}
		}
		
		if(birusCnt != birusTotalCnt) return Integer.MAX_VALUE;
		return depth - 1;
	}
	
	static boolean OOB(int r, int c) { return r < 0 || r >= N || c < 0 || c >= N; }
}