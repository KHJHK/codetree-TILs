import java.util.*;
import java.io.*;

public class Main {
	
	static class Dice{
		int num; //현재 바닥 숫자
		Deque<Integer> flipCycle = new ArrayDeque<>();
		int top; //현재 번호 기준 위 숫자
		int bottom; //현재 번호 기준 아래 숫자
		
		void init() {
			num = 6;
			top = 5;
			bottom = 2;
			flipCycle.add(3);
			flipCycle.add(1);
			flipCycle.add(4);
		}
		
		void flip() {
			flipCycle.add(num);
			num = flipCycle.poll();
		}
		
		void turn(){
			int tempTop = top;
			int tempBottom = bottom;
			top = flipCycle.poll();
			bottom = flipCycle.pollLast();
			flipCycle.addFirst(tempBottom);
			flipCycle.addLast(tempTop);
		}
		
		void reverse() {
			int first = flipCycle.pollFirst();
			int last = flipCycle.pollLast();
			flipCycle.addFirst(last);
			flipCycle.addLast(first);
		}
	}
	
	static Dice dice = new Dice();
	static int N, M;
	static int[] dr = {0, 1, 0, -1};
	static int[] dc = {1, 0, -1, 0};
	static int[][] board;
	
	static int r, c, dir;
	
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		board = new int[N][N];
		dice.init();
		
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < N; j++) board[i][j] = Integer.parseInt(st.nextToken());
		}
		
		int point = 0;
		
		for(int i = 0; i < M; i++) {
			moveDice();
			turnDice();
			point += getPoint();
		}
		
		System.out.println(point);
	}
	
	static void moveDice() {
		int nr = r + dr[dir];
		int nc = c + dc[dir];
		
		if(OOB(nr, nc)) {
			dice.reverse();
			dir = (dir + 2) % 4;
			moveDice();
		}
		
		dice.flip();
		r = nr;
		c = nc;
	}
	
	static void turnDice() {
		if(dice.num > board[r][c]) {
			dice.turn();
			dir = (dir + 1) % 4;
		}
	}
	
	static int getPoint() {
		boolean[][] visited = new boolean[N][N];
		Queue<int[]> queue = new ArrayDeque<>();
		queue.add(new int[] {r, c});
		visited[r][c] = true;
		int targetNum = board[r][c];
//		System.out.printf("%d | (%d, %d)\n", targetNum, r, c);
		int cnt = 1; //현재 칸 포함하고 시작하니 1부터 시작
		
		while(!queue.isEmpty()) {
			int[] now = queue.poll();
			
			for(int d = 0; d < 4; d++) {
				int nr = now[0] + dr[d];
				int nc = now[1] + dc[d];
				
				if(OOB(nr, nc)) continue; //board 나가면 continue
				if(visited[nr][nc]) continue; //이미 방문했으면 continue
				if(board[nr][nc] != targetNum) continue; //타겟 숫자와 다른 숫자면 continue
				
				visited[nr][nc] = true;
				queue.add(new int[] {nr, nc});
				cnt++; //한 칸이 추가된거니 +1
			}
		}
		
		return targetNum * cnt;
	}
	
	static boolean OOB(int r, int c) { return r < 0 || r >= N || c < 0 || c >= N; }
}