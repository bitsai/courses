t(X, nil, nil).
t(X, t(L, LL, LR), nil).
t(X, nil, t(R, RL, RR)).
t(X, t(L, LL, LR), t(R, RL, RR)).

% No children
isotree(t(X, nil, nil), t(X, nil, nil)).

% One child
isotree(t(X, A, nil), t(X, B, nil)) :- isotree(A, B).
isotree(t(X, A, nil), t(X, nil, B)) :- isotree(A, B).
isotree(t(X, nil, A), t(X, nil, B)) :- isotree(A, B).
isotree(t(X, nil, A), t(X, B, nil)) :- isotree(A, B).

% Two children
isotree(t(X, A, B), t(X, C, D)) :- isotree(A, C), isotree(B, D).
isotree(t(X, A, B), t(X, C, D)) :- isotree(A, D), isotree(B, C).
