import java.util.*;
import java.io.*;

public class Main {
	public static class Catcher{
		int r;
		int c;
		int d = 1;
		int moveMax = 1; //현재 방향으로 몇 칸 이동해야 하는지 세기 위한 변수
		int moveCnt = 0; //현재 방향으로 현재까지 몇 번 이동했는지 세기 위한 변수
		int turnMax = 2; //몇 번 회전 후 moveMax를 변경할지에 대한 변수 
		int turnCnt = 0; //현재까지 몇 번 회전했는지 저장하는 변수
		
		void init(boolean isMoveOutSide) {
			moveCnt = 0; 
			turnCnt = 0;
			if(isMoveOutSide) {
				d = 0;
				moveMax = 1; 
				turnMax = 2; 
			}
			else {
				d = 2;
				moveMax = N - 1; 
				turnMax = 3; 
			}
		}
		
		void moveToOutSide() {
			r += dr[d];
			c += dc[d];
			
			if(++moveCnt == moveMax) { //회전해야 하는 지점까지 이동했으면 회전
				moveCnt = 0;
				turn(true);
				if(++turnCnt == turnMax) { //일정 횟수 이상 회전했다면 이동 거리 늘리기(달팽이)
					turnCnt = 0;
					moveMax++;
					if(moveMax == N - 1) turnMax = 3; //마지막에는 회전 3번까지 가능
				}
			}
			
			if(r == 0 && c == 0) d = 2;
		}
		
		void moveToInSide() {
			r += dr[d];
			c += dc[d];
			
			if(++moveCnt == moveMax) { //회전해야 하는 지점까지 이동했으면 회전
				moveCnt = 0;
				turn(false);
				if(++turnCnt == turnMax) { //일정 횟수 이상 회전했다면 이동 거리 늘리기(달팽이)
					turnCnt = 0;
					moveMax--;
					turnMax = 2; //마지막에는 회전 3번까지 가능
				}
			}
			
			if(r == N/2 && c == N/2) d = 0;
		}
		
		void turn(boolean isMoveOutSide) {
			if(isMoveOutSide) d = (d + 1) % 4;
			else{
				if(--d == -1) d = 3;
			}
			
		}
	}
	
	public static class Player{
		int r;
		int c;
		int d;
		boolean isCatched = false;
		
		Player(int r, int c, int d){
			this.r = r;
			this.c = c;
			this.d = d;
		}
		
		void move() {
			int nr = r + dr[d];
			int nc = c + dc[d];
			
			if(OOB(nr, nc)) { //격자 밖으로 나가면 방향 바꿔서 다시 이동
				turn();
				move();
				return;
			}
			
			if(nr == catcher.r && nc == catcher.c) return; //술래랑 겹치는 위치면 이동 X
			r = nr;
			c = nc;
		}
		
		void turn() {
			d = (d + 2) % 4;
		}
	}
	
	static int N, M, H, K, score;
	static int[] dr = {-1, 0, 1, 0};
	static int[] dc = {0, 1, 0, -1};
	static boolean[][] treeMap; //나무 위치 저장용 map
	static Catcher catcher = new Catcher();
	static Player[] playerArr;

	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		H = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		treeMap = new boolean[N][N];
		playerArr = new Player[M];
		catcher.r = N / 2;
		catcher.c = N / 2;
		
		for(int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			int row = Integer.parseInt(st.nextToken()) - 1;
			int col = Integer.parseInt(st.nextToken()) - 1;
			int dir = Integer.parseInt(st.nextToken());
			playerArr[i] = new Player(row, col, dir);
		}
		
		for(int i = 0; i < H; i++) {
			st = new StringTokenizer(br.readLine());
			int row = Integer.parseInt(st.nextToken()) - 1;
			int col = Integer.parseInt(st.nextToken()) - 1;
			treeMap[row][col] = true;
		}
		
		
		boolean isCatcherMoveOutSide = true;
		for(int k = 1; k <= K; k++) {
			//도망
			playerMove();
			
//			//술래 이동
			if(catcher.r == N / 2 && catcher.c == N / 2) {
				isCatcherMoveOutSide = true;
				catcher.init(isCatcherMoveOutSide);
			}
			if(catcher.r == 0 && catcher.c == 0) {
				isCatcherMoveOutSide = false;
				catcher.init(isCatcherMoveOutSide);
			}
			
			if(isCatcherMoveOutSide) catcher.moveToOutSide();
			else catcher.moveToInSide();
			
//			//도망자 잡기
			score += catchPlayer() * k;
		}
		
		System.out.println(score);
	}
	
	public static int catchPlayer() {
		int cnt = 0; //잡힌 도망자 카운트
		
		int cr = catcher.r;
		int cc = catcher.c;
		int d = catcher.d;
		
		
		for(int i = 0; i < 3; i++) {
			int nr = cr + (i * dr[d]);
			int nc = cc + (i * dc[d]);
			
			for(Player p : playerArr) {
				if(p.isCatched) continue; //이미 잡혔으면 skip
				
				if(nr == p.r && nc == p.c) { //만약 잡히는 범위 안에 있으면
					if(!treeMap[nr][nc]) { //나무에 가려졌는지 확인
						p.isCatched = true; //잡힘 표시
						cnt++; 
					}
					continue;
				}
			}
		}
		
		return cnt;
	}
	
	public static void playerMove() {
		for(Player p : playerArr) {
			if(p.isCatched) continue; //이미 잡혔으면 skip
			if(isMoveAble(p.r, p.c)) p.move();
		}
	}
	
	public static boolean isMoveAble(int r, int c) {
		return Math.abs(catcher.r - r) + Math.abs(catcher.c - c) <= 3; 
	}
	
	public static boolean OOB(int r, int c) { return r < 0 || r >= N || c < 0 || c >= N; }

}