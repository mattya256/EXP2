int a(int,int),z(int,int);
void b(int[],int,int),main();

func int a(int c, int d) {
	c = c + d ;
	return c;
}

func void b(int c[], int d, int e) {
	d = c[1] + e;
}

func int z(int c, int d) {
	c = c + d ;
	return c;
}

func void main() {
	int i,j[10];
	i=2;
	j[3]=call a(i,5) + call z(2,2);
	call b(&j, j[3], 9); 
}