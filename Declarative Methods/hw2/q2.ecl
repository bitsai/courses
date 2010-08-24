:- lib(ic).
:- lib(branch_and_bound).
:- lib(ic_edge_finder).

a(N) :-
	Triangles = [A, B, C, D, E, F, G, H, I],
	Triangles #:: 1..9,
	alldifferent(Triangles),
	A + B + C + D #= N,
	B + E + F + G #= N,
	D + G + H + I #= N,
	flatten([Triangles, N], AllVars),
	labeling(AllVars),
	% printf("%d %d %d %d %d %d %d %d %d %n", Triangles),
	printf("%d%n", N).

b(Digits) :-
	Digits = [X1, X2, X3, Y1, Y2, Y3],
	Digits #:: 1..9,
	6 * (X1 * 100000 + X2 * 10000 + X3 * 1000 + Y1 * 100 + Y2 * 10 + Y3) #= (Y1 * 100000 + Y2 * 10000 + Y3 * 1000 + X1 * 100 + X2 * 10 + X3),
	labeling(Digits),
	printf("%d%d%d, %d%d%d%n", Digits).

c(Sides) :-
	Sides = [A, B, C],
	Sides #:: 1..200,
	A #= B,
	A #=< B + C,
	B #=< A + C,
	C #=< A + B,
	6 * (A + B + C) $= sqrt((A + (B + C)) * (C - (A - B)) * (C + (A - B)) * (A + (B - C))) / 4,
	labeling(Sides),
	printf("%d, %d, %d%n", Sides).

d(Digits) :-
	Digits = [Digit1, Digit2, Digit3, Digit4, Digit5],
	Digit1 #:: 1..9,
	Digit2 #:: 0..9,
	Digit3 #:: 0..9,
	Digit4 #:: 0..9,
	Digit5 #:: [0, 2, 4, 6, 8],
	Digit1 + Digit2 + Digit3 + Digit4 #= Digit5,
	labeling(Digits),
	printf("%d%d%d%d%d%n", Digits).

e(Square) :-
	Digits = [A, B, C, D, E, F, G, H, I, J],
	Digits #:: 0..9,
	alldifferent(Digits),
	integers([Square, Root]),
	Square #= A * 10^9 + B * 10^8 + C * 10^7 + D * 10^6 + E * 10^5 + F * 10^4 + G * 10^3 + H * 10^2 + I * 10 + J,
	Square #= Root * Root,
	minimize(labeling(Digits), Square),
	printf("%d%n", Square).