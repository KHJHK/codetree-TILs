import java.util.*;
import java.io.*;

public class Main {	
	static int N, M, K;
	static int er, ec, moveCnt, pcnt; // moveCnt로 이동거리 총 합 저장, pcnt로 player 수 저장
	static int[] dr = {-1, 1, 0, 0};
	static int[] dc = {0, 0, -1, 1};
	static int[][] players; //player 좌표 저장용
	static int[][] map;

	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		map = new int[N + 1][N + 1];
		players = new int[M][2];
		moveCnt = 0;
		pcnt = M;
		
		for(int i = 1; i < N + 1; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 1; j < N + 1; j++) map[i][j] = Integer.parseInt(st.nextToken());
		}
		
		for(int i = 0; i < M; i++) {	
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			players[i] = (new int[] {r, c});
			map[r][c] = -1;
		}
		
		st = new StringTokenizer(br.readLine());
		er = Integer.parseInt(st.nextToken()); //exit row
		ec = Integer.parseInt(st.nextToken()); //exit col
		map[er][ec] = -2;
		
		int time = 0;
		while(pcnt > 0 && time++ < K) {
			movePlayer();
			findEndrotateSquare();
		}
		System.out.println(moveCnt);
		System.out.println(er + " " + ec);
		
	}
	
	static void movePlayer() {
		for(int p = M - 1; p >= 0; p--) {
			int r = players[p][0];
			int c = players[p][1];
			
			if(r == 0 && c == 0) continue;
			
			for(int d = 0; d < 4; d++) {
				int nr = r + dr[d]; //이동할 좌표 r nextRow
				int nc = c + dc[d]; //이동할 좌표 c nextCol
				
				if(OOB(nr, nc) || map[nr][nc] > 0 || isFarFromExit(r, c, nr, nc)) continue; //맵을 나감, 이동할 칸이 벽임, 거리가 멀어짐 => 세 가지 경우 continue
				
				moveCnt++;
				if(map[nr][nc] == -2) {
					players[p][0] = 0;
					players[p][1] = 0;
					pcnt--;
				}
				else{
					players[p][0] = nr;
					players[p][1] = nc;
				}
				map[r][c] = 0;
				break; //이동시 다음 player로 넘어가기
			}
		}
		
		for(int p = 0; p < M; p++) {
			int r = players[p][0];
			int c = players[p][1];
			map[r][c] = -1;
		}
		
	};
	
	static void findEndrotateSquare() {
		int squareLen = 2;
		while(squareLen < N) {
			for(int r = 1; r <= N - squareLen + 1; r++) {
				for(int c = 1; c <= N - squareLen + 1; c++) {
					if(isrotateAbleSquare(r, c, squareLen)) return;
				}
			}
			squareLen++;
		}
	}
	
	static boolean isrotateAbleSquare(int row, int col, int len) {
		//player와 exit가 정사각형 안에 있는지 확인
		boolean isPlayerIn = false;
		boolean isExitIn = false;
		
		for(int r = row; r < row + len; r++) {
			for(int c = col; c < col + len; c++) {
				if(map[r][c] == -1) isPlayerIn = true;
				if(map[r][c] == -2) isExitIn = true;
				if(isPlayerIn && isExitIn) {
					rotateSquare(row, col, len);
					return true;
				}
			}
		}
		
		return false;
	}
	
	static void rotateSquare(int row, int col, int len) {// 실제 회전 함수
		List<Integer> rotatePlayers = new ArrayList<>();
		
		int[][] temp = new int[len][len];
		for(int r = row; r < row + len; r++) {
			for(int c = col; c < col + len; c++) {
				if(map[r][c] > 0) map[r][c]--;
				if(map[r][c] == -1) {
					for(int p = 0; p < M; p++) {
						if(players[p][0] == r && players[p][1] == c) rotatePlayers.add(p);
					}
				}
				int tr = r - row;
				int tc = c - col;
				temp[tc][len - 1 - tr] = map[r][c];
			}
		}
		
		for(int r = row; r < row + len; r++) {
			for(int c = col; c < col + len; c++) {
				int tr = r - row;
				int tc = c - col;
				map[r][c] = temp[tr][tc];
				if(map[r][c] == -2) {
					er = r;
					ec = c;
				}
				if(map[r][c] == -1) {
					//회전 처리
					for(int idx : rotatePlayers) {
						//c - c시작점 + r시작점, len - 1 - (r-r시작점) + c시작점
						int pr = players[idx][0];
						int pc = players[idx][1];
						if(r == pc - col + row && c == len - 1 - (pr - row) + col) {
							players[idx][0] = r;
							players[idx][1] = c;
						}
					}
				}
			}
		}
	}
	
	static boolean isFarFromExit(int r, int c, int nr, int nc) { return Math.abs(r - er) + Math.abs(c - ec) <= Math.abs(nr - er) + Math.abs(nc - ec); }
	
	static boolean OOB(int r, int c) { return r > N || r <= 0 || c > N || c <= 0; }
	
	static void printMap() { 
		for(int m = 1; m < N + 1; m++) {
			for(int mm = 1; mm < N + 1; mm++) {
				System.out.printf("%5d", map[m][mm]);
			}System.out.println();
		}
		System.out.println();
	}

}