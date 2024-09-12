import java.util.*;
import java.io.*;

public class Main {
	static int N, point;
	static int[][] map1 = new int[6][4];
	static int[][] map2 = new int[6][4];
	static int[][] block1;
	static int[][] block2;
	static Stack<Integer> delRowStack = new Stack<>();

	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		N = Integer.parseInt(br.readLine());
		block1 = new int[N][3];
		block2 = new int[N][3];
		
		for(int i = 0; i < N; i++) {
			StringTokenizer st = new StringTokenizer(br.readLine());
			for(int j = 0; j < 3; j++) block1[i][j] = Integer.parseInt(st.nextToken());
			block2[i] = lotateBlock(block1[i][0], block1[i][1], block1[i][2]);
		}
		
		//테트리스 진행
		for(int cnt = 0; cnt < N; cnt++) {
			//1번 테트리스 진행
			drop(cnt, map1, block1);
			pointCheck(map1);
			deleteLine(map1);
			lineOverCheck(map1);
			deleteLine(map1);
			
			//2번 테트리스 진행
			drop(cnt, map2, block2);
			pointCheck(map2);
			deleteLine(map2);
			lineOverCheck(map2);
			deleteLine(map2);
		}

        System.out.println(point);
		System.out.println(checkBlockSum());

	}
	
	//초기 블록 시계방향으로 회전
	//회전 후 오른쪽 방향 테트리스도 아래로 떨어지는 테트리스처럼 처리 가능
	static int[] lotateBlock(int type, int r, int c) {
		int len = 4;
		int type2 = type;
		//2번 타입이면 3번 타입으로 변경됨
		if(type == 2) type2 = 3;
		else if(type == 3) {//3번 타입이면 2번 타입으로 변경됨, 시작 기준점도 (r+1, c)로 변함
			type2 = 2;
			r += 1;
		}
		
		int r2 = c;
		int c2 = len - 1 - r;
		
		int[] lotate = new int[] {type2, r2, c2};
		return lotate;
	}
	
	static int drop(int idx, int[][] map, int[][] blocks) { 
		
		int[] block = blocks[idx];
		int type = block[0];
		int r = 0;
		int c = block[2];
		
		switch(type) {
		case 1:	//한 칸 미노
			map[r][c] = 1;
			for(int i = 0; i < 5; i++) { //row = 0부터 시작, 최대 5칸 떨어짐
				int nr = r + 1;
				if(map[nr][c] == 1) return 1; //아래가 막힌 경우 종료
				
				//막히지 않은 경우 진행
				map[nr][c] = 1;
				map[r][c] = 0;
				r = nr;
			}
			return 1;
		case 2: //가로 미노
			int c2 = c + 1; //오른쪽 칸에 있는 미노의 col 좌표(row는 같음)
			map[r][c] = 1;
			map[r][c2] = 1;
			for(int i = 0; i < 5; i++) { //row = 0부터 시작, 최대 5칸 떨어짐
				
				int nr = r + 1;
				if(map[nr][c] == 1 || map[nr][c2] == 1) return 1; //아래가 막힌 경우 종료
				
				//막히지 않은 경우 진행
				map[nr][c] = 1;
				map[nr][c2] = 1;
				map[r][c] = 0;
				map[r][c2] = 0;
				r = nr;
			}
			return 1;
		case 3: //세로 미노
			int r2 = r + 1; //아래 칸에 있는 미노의 row 좌표(row는 같음)
			map[r][c] = 1;
			map[r2][c] = 1;
			for(int i = 0; i < 4; i++) { //row = 0부터 시작, 최대 4칸 떨어짐(세로로 2칸이기 때문)
				int nr = r2 + 1;
				if(map[nr][c] == 1) return 1; //아래가 막힌 경우 종료
				
				//막히지 않은 경우 진행
				map[nr][c] = 1;
				map[r][c] = 0;
				r = r2;
				r2 = nr;
			}
			return 1;
		}
		
		return 0; //미노 케이스에 걸리지 않으 경우 0 반환 => 에러케이스
	}
	
	static void pointCheck(int[][] map) {		
		for(int r = 2; r < 6; r++) {
			int cnt = 0;
			for(int c = 0; c < 4; c++) {
				if(map[r][c] == 1) cnt++;
			}
			if(cnt == 4) {
				delRowStack.push(r);
				point++;
			}
		}
	}
	
	static void deleteLine(int[][] map) {
		while(!delRowStack.isEmpty()) {
			int row = delRowStack.pop();
			shiftLine(row, map);
		}
	}

	static void shiftLine(int row, int[][] map) {
		for(int r = row - 1; r >= 0; r--) {
			for(int c = 0; c < 4; c++) {
				map[r + 1][c] = map[r][c];
				if(r == 0) map[r][c] = 0; //마지막 줄 초기화 시켜주기
			}
		}
	}
	
	static void lineOverCheck(int[][] map) {
		for(int r = 0; r < 2; r++) {
			for(int c = 0; c < 4; c++) {
				if(map[r][c] == 1) {
					delRowStack.push(5); //여백 공간에 블록 있으면 한줄 삭제 확정
					if(r == 0) delRowStack.push(4); //여백 공간에 두 칸 모두 블록이 남아있다면 두줄 삭제
					return;
				}
			}
		}
	}
	
	static int checkBlockSum() {
		int sum = 0;
		for(int r = 2; r < 6; r++) {
			for(int c = 0; c < 4; c++) sum += map1[r][c] + map2[r][c];
		}
		return sum;
	}
	
	static void printMap(int[][] map) {
		for(int[] m : map) System.out.println(Arrays.toString(m));
	}

}