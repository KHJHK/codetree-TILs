import java.util.*;
import java.io.*;

public class Main {
	static class Tag{
		int r = 0;
		int c = 0;
		int dir;
		
		Tag(){};
		Tag(Tag tag){
			this.r = tag.r;
			this.c = tag.c;
			this.dir = tag.dir;
		}
	}
	static class Runner {
		int r;
		int c;
		int num;
		int dir;
		boolean isCatched;
		
		public Runner(int r, int c, int num, int dir) {
			this.r = r;
			this.c = c;
			this.num = num;
			this.dir = dir;
			isCatched = false;
		}
		
		void changeDir(){
			dir = (dir + 1) % 8;
		}
	}
	
	static int maxPoint;
	static int[] dr = {-1, -1, 0, 1, 1, 1, 0, -1};
	static int[] dc = {0, -1, -1, -1, 0, 1, 1, 1};
	static int[][] originalMap = new int[4][4]; //비트마스킹 사용하기
	static Runner[] originalRunners = new Runner[16 + 1]; //1~16 사용, 0번은 사용 x
	static Tag originalTag = new Tag();

	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		for(int i = 0; i < 4; i++) {
			StringTokenizer st = new StringTokenizer(br.readLine());
			for(int j = 0; j < 4; j++) {
				int num = Integer.parseInt(st.nextToken());
				int dir = Integer.parseInt(st.nextToken()) - 1;
				originalRunners[num] = new Runner(i, j, num, dir);
				originalMap[i][j] += num;
				if(i == 0 && j == 0) init(num, dir);
			}
		}
		
		runnerMove(maxPoint, originalMap, originalRunners, originalTag);
		System.out.println(maxPoint);
	}
	
	public static void runnerMove(int point, int[][] map, Runner[] runners, Tag tag) {
		int[][] tempMap = new int[4][];
		for(int i = 0; i < 4; i++) tempMap[i] = Arrays.copyOf(map[i], 4);
		Runner[] tempRunners = new Runner[17];
		for(int i = 1; i <= 16; i++) {
			Runner tempRunner = runners[i];
			tempRunners[i] = new Runner(tempRunner.r, tempRunner.c, tempRunner.num, tempRunner.dir);
			tempRunners[i].isCatched = tempRunner.isCatched;
		}
		
		for(int i = 1; i <= 16; i++) {
			if(tempRunners[i].isCatched) continue;
			int r = tempRunners[i].r; //초기 방향
			int c = tempRunners[i].c; //초기 방향
			int fd = tempRunners[i].dir; //초기 방향
			int nr = r + dr[fd];
			int nc = c + dc[fd];
			
			if(!OOB(nr, nc) && tempMap[nr][nc] != -1) {
				if(tempMap[nr][nc] != 0) { //다른 말이 있는 경우
					int temp = tempMap[nr][nc]; //해당 칸의 말 번호 저장
					tempMap[nr][nc] = i; // 해당 칸으로 이동
					tempMap[r][c] = temp; // 현재 칸에 이동하는 칸 말 번호 저장
					tempRunners[i].r = nr; // 현재 말 좌표 설정
					tempRunners[i].c = nc;
					tempRunners[temp].r = r; //변경된 칸의 말 좌표 설정
					tempRunners[temp].c = c;
				}
				
				else if(tempMap[nr][nc] == 0){ //빈칸인 경우
					tempMap[nr][nc] = i;
					tempMap[r][c] = 0;
					tempRunners[i].r = nr;
					tempRunners[i].c = nc;
				}
				
				continue;
			}
			
			tempRunners[i].changeDir();
			while(fd != tempRunners[i].dir) {
				int d = tempRunners[i].dir;
				nr = r + dr[d];
				nc = c + dc[d];
				
				if(OOB(nr, nc) || tempMap[nr][nc] == -1) {
					tempRunners[i].changeDir();
					continue;
				}
				
				if(tempMap[nr][nc] != 0) { //다른 말이 있는 경우
					int temp = tempMap[nr][nc];
					tempMap[nr][nc] = i;
					tempMap[r][c] = temp;
					tempRunners[i].r = nr;
					tempRunners[i].c = nc;
					tempRunners[temp].r = r;
					tempRunners[temp].c = c;
				}
				else if(tempMap[nr][nc] == 0){ //빈칸인 경우
					tempMap[nr][nc] = i;
					tempMap[r][c] = 0;
					tempRunners[i].r = nr;
					tempRunners[i].c = nc;
				}
				
				break;
			}
		}
		
		tagMove(point, tempMap, tempRunners, tag);
	}
	
	public static void tagMove(int point, int[][] map, Runner[] runners, Tag tag) {
		int d = tag.dir;
		int r = tag.r;
		int c = tag.c;
		
		int nr = r + dr[d];
		int nc = c + dc[d];
		
		while(!OOB(nr, nc)) {
			if(map[nr][nc] == 0) break;
			
			int[][] tempMap = new int[4][];
			for(int i = 0; i < 4; i++) tempMap[i] = Arrays.copyOf(map[i], 4);
			Runner[] tempRunners = new Runner[17];
			for(int i = 1; i <= 16; i++) {
				Runner tempRunner = runners[i];
				tempRunners[i] = new Runner(tempRunner.r, tempRunner.c, tempRunner.num, tempRunner.dir);
				tempRunners[i].isCatched = tempRunner.isCatched;
			}
			Tag tempTag = new Tag(tag);
			
			int plusPoint = tempMap[nr][nc];
			tempMap[r][c] = 0;
			
			tempRunners[tempMap[nr][nc]].isCatched = true;
			tempTag.r = nr;
			tempTag.c = nc;
			tempTag.dir = tempRunners[tempMap[nr][nc]].dir;
			tempMap[nr][nc] = -1;
			tempMap[r][c] = 0;
			
			int nnr = nr + dr[tempTag.dir];
			int nnc = nc + dc[tempTag.dir];
			if(OOB(nnr, nnc) || tempMap[nnr][nnc] == 0) {
				maxPoint = Math.max(maxPoint, point + plusPoint); //만약 현재 이동 후 끝난다면, 포인트 갱신
				nr += dr[d];
				nc += dc[d];
				continue;
			}
			else runnerMove(point + plusPoint, tempMap, tempRunners, tempTag); //끝나지 않는다면 말 움직이기
			
			nr += dr[d];
			nc += dc[d];
		}
		
		maxPoint = Math.max(maxPoint, point);
	}
	
	public static void init(int num, int dir) {
		originalRunners[num].isCatched = true;
		originalTag.dir = dir;
		maxPoint += num;
		originalMap[0][0] = -1;
	}
	
	public static boolean OOB(int r, int c) { return r < 0 || r >= 4 || c < 0 || c >= 4; }
	
}