:- lib(branch_and_bound).

% Answer to 4a
t(X, nil, nil).
t(X, t(L, LL, LR), nil).
t(X, nil, t(R, RL, RR)).
t(X, t(L, LL, LR), t(R, RL, RR)).

inorder1(t(X, nil, nil), [X]).
inorder1(t(X, Tree, nil), Output) :- inorder1(Tree, T), append(T, [X], Output).
inorder1(t(X, nil, Tree), Output) :- inorder1(Tree, T), append([X], T, Output).
inorder1(t(X, Left, Right), Output) :- inorder1(Left, L), inorder1(Right, R), append(L, [X], Mid), append(Mid, R, Output).

% Answer to 4b
inorder2(t(X, nil, nil), [X]).
inorder2(t(X, nil, Tree), Input) :- append([X], T, Input), inorder2(Tree, T).
inorder2(t(X, Tree, nil), Input) :- append(T, [X], Input), inorder2(Tree, T).
inorder2(t(X, Left, Right), Input) :- append(Mid, R, Input), append(L, [X], Mid), inorder2(Right, R), inorder2(Left, L).

% Answer to 4c
total_depth(t(X, nil, nil), 0).
total_depth(t(X, Tree, nil), D) :- total_depth(Tree, T), D is T + 1.
total_depth(t(X, nil, Tree), D) :- total_depth(Tree, T), D is T + 1.
total_depth(t(X, Left, Right), D) :- total_depth(Left, L), total_depth(Right, R), L =< R, D is R + 1.
total_depth(t(X, Left, Right), D) :- total_depth(Left, L), total_depth(Right, R), L > R, D is L + 1.

balanced(Tree, List) :- findall(T, inorder2(T, List), Ts), minimize((member(Tree, Ts), Cost is total_depth(Tree)), Cost).

% Answer to 4d [Extra Credit]
split(List, Mid, Head, Tail) :- append(Head, [Mid|Tail], List), length(Head, X), length([Mid|Tail], Y), Y > X - 1, Y =< X + 1.

balanced2(t(X, [], []), [X]).
balanced2(t(Mid, Tree, []), List) :- split(List, Mid, Head, []), balanced2(Tree, Head).
balanced2(t(Mid, [], Tree), List) :- split(List, Mid, [], Tail), balanced2(Tree, Tail).
balanced2(t(Mid, Left, Right), List) :- split(List, Mid, Head, Tail), balanced2(Left, Head), balanced2(Right, Tail).
