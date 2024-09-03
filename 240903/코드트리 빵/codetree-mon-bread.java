import java.util.*;
import java.io.*;


public class Main {
	
	static class Person {
		int r = -1; //현재 좌표
		int c = -1; //현재 좌표
		int tr; //target r - 편의점 row 좌표
		int tc; //target c - 편의점 col 좌표
	}
	
	static int N, M;
	static Person[] people;
	static int[] dr = {-1, 0, 0, 1};
	static int[] dc = {0, -1, 1, 0};
	static int map[][]; //0은 빈칸, 1은 베이스캠프, 2는 막힌길
	
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		map = new int[N][N];
		people = new Person[M];
		
		for(int i = 0; i < M; i++) people[i] = new Person();
		
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < N; j++) map[i][j] = Integer.parseInt(st.nextToken());
		}
		
		for(int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			people[i].tr = Integer.parseInt(st.nextToken()) - 1;
			people[i].tc = Integer.parseInt(st.nextToken()) - 1;
		}
		
		int time = 0; //idx로 많이 사용될 예정
		int arrived = 0;
		
		while(arrived < M) { //조건 : blockStore(time) != M;
			//1. 현재 시간대 이전 사람 이동 - 베이스캠프와 겹치는 경우 방지를 위해 이동 먼저
			movePeople(time);
			//2. 사람이 도착한 편의점 막기, 현재 시간대 이전 사람들의 목적지만 확인하면 됨
			arrived = blockStore();
			
			//3. 현재 시간대 사람 베이스캠프 지정
			if(time < M) getBasecamp(time);
			
			time++;
		}
		

		System.out.println(time);
	}
	
	static void movePeople(int time){
		if(time > M) time = M; // 시간이 이동할 사람 수보다 커지면 idx값 조정을 위해 time값을 M으로 조정
		//현재 시간 이전까지의 사람들 모두 이동
		for(int idx = 0; idx < time; idx++) {
			int startRow = people[idx].r;
			int startCol = people[idx].c;
			
			if(startRow == people[idx].tr && startCol == people[idx].tc) continue; //이미 도착한 경우라면 bfs 스킵
			
			boolean[][] visited = new boolean[N][N];
			Queue<int[]> queue = new ArrayDeque<>();
			boolean isFirstMove = true;
			
			queue.offer( new int[] {startRow, startCol, startRow, startCol} ); //각각 이동할 사람의 현재 row, col, 첫 이동 저장용 row, col
			visited[startRow][startCol] = true;
			
			W:while(!queue.isEmpty()) {
				int[] now = queue.poll();
				//현재 좌표
				int r = now[0];
				int c = now[1];
				//해당 이동의 첫 이동 좌표
				int fr = now[2];
				int fc = now[3];
				
				for(int d = 0; d < 4; d++) {
					int nr = r + dr[d];
					int nc = c + dc[d];
					
					if(OOB(nr, nc) || map[nr][nc] == 2 || visited[nr][nc]) continue; //격자 나가거나, 막힌 길이거나, 방문했으면 넘기기
					if(isFirstMove) { //첫 이동 좌표 저장
						fr = nr;
						fc = nc;
					}
					if(nr == people[idx].tr && nc == people[idx].tc) { //편의점 도착 최단거리를 구하면, 사람을 해당 이동의 첫 이동 좌표(now[2], now[3])로 이동하기
						people[idx].r = fr;
						people[idx].c = fc;
						break W;
					}
					
					visited[nr][nc] = true;
					queue.offer(new int[] {nr, nc, fr, fc});
				}
				
				isFirstMove = false; //한 번 이상 이동하면 isFirstMove = false로 첫 이동 감지 종료
			}
		}
	}
	
	static void getBasecamp(int time){
		int idx = time;
		
		boolean[][] visited = new boolean[N][N];
		Queue<int[]> queue = new ArrayDeque<>();
		
		queue.offer( new int[] { people[idx].tr, people[idx].tc } ); //편의점 좌표 넣기 - bfs 시작점
		visited[people[idx].tr][people[idx].tc] = true;
		boolean isBasecamp = false;
		List<int[]> basecampList = new ArrayList<>();
		
		while(!queue.isEmpty() && !isBasecamp) {
			int[] now = queue.poll();
			int r = now[0];
			int c = now[1];
			
			for(int d = 0; d < 4; d++) {
				int nr = r + dr[d];
				int nc = c + dc[d];
				
				if(OOB(nr, nc) || map[nr][nc] == 2 || visited[nr][nc]) continue; //격자 나가거나, 막힌 길이거나, 방문했으면 넘기기
				
				if(map[nr][nc] == 1) { //베이스캠프면 해당 위치 저장
					isBasecamp = true;
					basecampList.add(new int[] {nr, nc});
					continue;
				}
				
				visited[nr][nc] = true;
				queue.offer(new int[] {nr, nc});
			}
		}
		
		int br = N;
		int bc = N;
		
		for(int[] basecamp : basecampList) {
			if(basecamp[0] < br || ( basecamp[0] == br && basecamp[1] < bc )) {
				br = basecamp[0];
				bc = basecamp[1];
			}
		}
		
		people[idx].r = br;
		people[idx].c = bc;
		map[br][bc] = 2;
	}
	
	static int blockStore() {
		int cnt = 0; //도착한 사람 수 체크
		
		for(Person p : people) {
			if(p.r == p.tr && p.c == p.tc) { //도착했으면 편의점 길 막고 카운트
				map[p.tr][p.tc] = 2;
				cnt++;
			}
		}
		return cnt;
	}
	
	static boolean OOB(int r, int c) { return r >= N || r < 0 || c >= N || c < 0; }

}