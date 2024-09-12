import java.util.*;
import java.io.*;

/**
 * - 문제
 * 한칸, 오른쪽 한칸, 아래로 한칸 길이의 테트리스 블록 있음
 * 해당 블록은 아래 or 오른쪽으로 떨어짐
 * 블록은 다른 블록과 만나면 그 자리를 유지
 * 4*4 칸 두 개에 테트리스 시작
 * 만약 쌓인 블록이 칸을 벗어난다면, 해당 블록의 높이만큼 밑에서부터 불록 지움
 * 라인이 꽉 찰 경우 라인 지움(행 / 열 모두 체크)
 * 만약 칸을 벗어나는 경우와 줄이 지워지는 경우가 동시에 존재하면, 줄이 지워지는 경우가 우선시됨
 * 줄을 지울때마다 +1점
 * 행동이 모두 끝나고 얻는 점수 구하기
 * 
 * - 풀이
 * 오른쪽 테트리스도 시계방향 90도 회전시켜서 진행
 * 테트리스가 진행되는 칸의 크기는 6*4로, 위에 두 칸은 초과칸
 * 1. 테트리스 진행 방식은 같음
 * 	1.1 미노 위치 및 종류 확인
 * 	1.2 미노 떨어트리기
 * 		1.2.1 세로 미노는 최하단 칸만 확인, 가로 미노는 좌,우칸의 하단칸 모두 확인하면서 떨어트리기
 * 		1.2.2 만약 하단 칸에 다른 미노가 있을 경우, 떨어트리기 종료
 * 	1.3 완성된 줄이 있는지 확인
 * 		1.3.1 완성된 줄이 있다면 점수 +1점(가로만 해당, 세로로 줄이 꽉 차도 삭제 X)
 * 		1.3.2 완성된 줄 지우기
 * 		1.3.3 만약 가로줄(row)이 삭제되었다면, 해당 row보다 위에 있는 모든 값들의 row값 +1;
 * 	1.4 초과칸에 미노가 있는지 확인
 * 		1.4.1 row값이 1 or 2인 칸에 미노가 있는지 확인
 * 		1.4.2 초과 칸이 없는 경우 종료
 * 		1.4.3 만약 미노가 있다면, 하단에서부터 초과한 값만큼 라인 삭제(row 1까지 있는 경우 2줄, 2까지 있는 경우 1줄 삭제)
 * 		1.4.4 삭제된 row 이하의 모든 칸에 존재하는 미노에 대하여 (row + 삭제된 줄 수) 만큼 진행
 * 2. 아래 방향 테트리스는 위와 같이 진행
 * 3. 오른쪽 테트리스는 시계방향 90도 회전 후 진행
 * 	3.1 놓는 위치 시계방향 90도 회전
 * 		3.1.1 회전시, 미노의 종류도 변경됨(3번 -> 2번, 2번 -> 3번)
 * 		3.1.2 회전시, 초기 미노를 놓는 위치값도 변경되니 조심해야함
 * 	3.2 1번 테트리시진행과 같은 방식으로 테트리스 진행
 *
 */

public class Main {
	static int N, point;
	static int[][] map1 = new int[6][4];
	static int[][] map2 = new int[6][4];
	static int[][] block1;
	static int[][] block2;
	static Queue<Integer> delRowQueue = new ArrayDeque<>();

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
//			System.out.printf("---1번 시작---\n", cnt + 1);
			drop(cnt, map1, block1);
			pointCheck(map1);
			deleteLine(map1);
			lineOverCheck(map1);
			deleteLine(map1);
//			printMap(map1);
//			System.out.println();
			
			//2번 테트리스 진행
//			System.out.println("---2번 시작---");
			drop(cnt, map2, block2);
			pointCheck(map2);
			deleteLine(map2);
			lineOverCheck(map2);
			deleteLine(map2);
//			printMap(map2);
//			System.out.println(point);
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
	
	/**
	 * 
	 * @param idx : 테트리스 진행중인 미노의 순번
	 * @param map : 현재 테트리스 진행중인 맵
	 * @param blocks : 현재 테트리스 진행중인 블록 배열
	 * @return : 이동이 종료된 현재 미노의 좌표값
	 */
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
	
	/**
	 * 
	 * @param map : 현재 테트리스 진행중인 맵
	 * @param type : 블록 종류
	 * @return : 포인트를 얻은 라인의 row 좌표값들 | 없다면 빈 배열
	 */
	static void pointCheck(int[][] map) {		
		for(int r = 2; r < 6; r++) {
			int cnt = 0;
			for(int c = 0; c < 4; c++) {
				if(map[r][c] == 1) cnt++;
			}
			if(cnt == 4) {
				delRowQueue.offer(r);
				point++;
			}
		}
	}
	
	/**
	 * 
	 * @param map : 줄 삭제가 진행되는 map
	 */
	static void deleteLine(int[][] map) {
		while(!delRowQueue.isEmpty()) {
			int row = delRowQueue.poll();
			shiftLine(row, map);
		}
	}
	
	/**
	 * 
	 * @param row : 당겨질 기준 row
	 * @param shiftCnt : shiftCnt 만큼 행 이동
	 * @param map : shift가 진행되는 map
	 */
	static void shiftLine(int row, int[][] map) {
		for(int r = row - 1; r >= 0; r--) {
			for(int c = 0; c < 4; c++) {
				map[r + 1][c] = map[r][c];
				if(r == 0) map[r][c] = 0; //마지막 줄 초기화 시켜주기
			}
		}
	}
	
	/**
	 * 
	 * @param map : 줄이 넘어갔는지 체크할 map
	 */
	static void lineOverCheck(int[][] map) {
		for(int r = 0; r < 2; r++) {
			for(int c = 0; c < 4; c++) {
				if(map[r][c] == 1) {
					if(r == 0) delRowQueue.offer(4); //여백 공간에 두 칸 모두 블록이 남아있다면 두줄 삭제
					delRowQueue.offer(5); //여백 공간에 블록 있으면 한줄 삭제 확정
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