#include <iostream>
#include <ctime>
#include <cstring>
#include <cmath>
#include <algorithm>
using namespace std;

#define MAX 80000000
int board[MAX];
int pos_diag[2 * MAX - 1]; // 主对角线上的皇后个数
int neg_diag[2 * MAX - 1]; // 反对角线上的皇后个数

// 生成[0, n - 1]的一个大随机数
int get_randindex(int n) {
    int sqrt_n = (int)sqrt(n);
    int high = rand() % sqrt_n, low = rand() % sqrt_n;
    return high * sqrt_n + low;
}

void random_start(int n) {
    // 随机产生一个新解,使每行、每列都只有一个皇后
    random_shuffle(board, board + n);
}

int random_start_qs4(int n, int c) {
    int m = n - c;
    for (int i = 0; i < n; ++i) board[i] = i;
    memset(pos_diag, 0, sizeof(int) * (2 * n - 1));
    memset(neg_diag, 0, sizeof(int) * (2 * n - 1));
    int conflicts = 0;
    // 首先在空闲列中随机放置m个皇后,保证无冲突
    for (int i = 0, last = n; i < m; ++i, --last) {
        // 从[i, n)中选j,保证不影响已放置的皇后
        int j = i + get_randindex(last);
        while (pos_diag[i - board[j] + n - 1] > 0 || neg_diag[i + board[j]] > 0) j = i + get_randindex(last);
        swap(board[i], board[j]);
        pos_diag[i - board[i] + n - 1]++;
        neg_diag[i + board[i]]++;
    }
    // 剩余c个皇后在空闲列中随便放置,不管是否产生冲突
    for (int i = m, last = c; i < n; ++i, --last) {
        int j = i + get_randindex(last);
        swap(board[i], board[j]);
        pos_diag[i - board[i] + n - 1]++;
        neg_diag[i + board[i]]++;
    }
    for (int i = 0; i < 2 * n - 1; ++i) {
        conflicts += pos_diag[i] > 1 ? pos_diag[i] - 1 : 0;
        conflicts += neg_diag[i] > 1 ? neg_diag[i] - 1 : 0;
    }
    return conflicts;
}


// 初始时冲突的皇后数量
int get_c(int n) {
    if (n <= 10) return n > 8 ? 8 : n;
    else if (n <= 100) return 30;
    else if (n <= 10000) return 50;
    else if (n <= 100000) return 80;
    return 100;
}

// int get_conflicts(int n) {
//     int ans = 0;
//     // 只有对角线上才有可能产生冲突
//     for (int i = 0; i < n - 1; ++i) {
//         for (int j = i + 1; j < n; ++j) {
//             if (j - i == abs(board[i] - board[j])) ans++;
//         }
//     }
//     return ans;
// }

int get_conflicts(int n) {
    int ans = 0;
    memset(pos_diag, 0, sizeof(int) * (2 * n - 1));
    memset(neg_diag, 0, sizeof(int) * (2 * n - 1));
    for (int i = 0; i < n; ++i) {
        pos_diag[i - board[i] + n - 1]++;
        neg_diag[i + board[i]]++;
    }
    for (int i = 0; i < 2 * n - 1; ++i) {
        ans += pos_diag[i] > 1 ? pos_diag[i] - 1 : 0;
        ans += neg_diag[i] > 1 ? neg_diag[i] - 1 : 0;
    }
    return ans;
}

// 交换第i和第j个皇后带来的冲突数变化
int swap_gain(int i, int j, int n) {
    int gain = 0;
    // 原来位置对应的对角线上皇后数需要减1
    // 如果原来对应的对角线上有冲突,则gain--
    if (neg_diag[i + board[i]] > 1) gain--;
    if (neg_diag[j + board[j]] > 1) gain--;
    if (pos_diag[i - board[i] + n - 1] > 1) gain--;
    if (pos_diag[j - board[j] + n - 1] > 1) gain--;
    // 同理可知新对应的对角线上有皇后,则gain++
    if (neg_diag[i + board[j]] > 0) gain++;
    if (neg_diag[j + board[i]] > 0) gain++;
    if (pos_diag[i - board[j] + n - 1] > 0) gain++;
    if (pos_diag[j - board[i] + n - 1] > 0) gain++;
    return gain;
}

// 由于交换而更新冲突表和皇后位置
void update_state(int i, int j, int n) {
    neg_diag[i + board[i]]--;
    neg_diag[j + board[j]]--;
    pos_diag[i - board[i] + n - 1]--;
    pos_diag[j - board[j] + n - 1]--;

    neg_diag[i + board[j]]++;
    neg_diag[j + board[i]]++;
    pos_diag[i - board[j] + n - 1]++;
    pos_diag[j - board[i] + n - 1]++;
    
    swap(board[i], board[j]);
}


void local_search_v0(int n) {
    bool restart = true;
    int curr;
    clock_t start = clock();
    while (true) {
        // 如果找不到更小的冲突数,重新生成随机解
        if (restart) {
            random_start(n);
            curr = get_conflicts(n);
        }
        if (curr == 0) break;
        restart = true;
        // 选择新状态
        for (int i = 0; i < n - 1; ++i) {
            for (int j = i + 1; j < n; ++j) {
                swap(board[i], board[j]);
                int tmp = get_conflicts(n);
                // 如果交换后冲突数减少,则接受这种交换
                if (tmp < curr) {
                    curr = tmp;
                    restart = false;
                }
                else swap(board[i], board[j]);
            }
        }
    }
    clock_t end = clock();
    cout << "solved in " << (double)(end - start) / CLOCKS_PER_SEC << "s" << endl;
}

// 优化:引入冲突表,计算冲突数只需O(1)
void local_search_v1(int n) {
    bool restart = true;
    int curr;
    clock_t start = clock();
    while (true) {
        if (restart) {
            random_start(n);
            curr = get_conflicts(n);
        }
        if (curr == 0) break;
        restart = true;
        // 随机交换两个皇后,尝试次数不超过邻域大小
        long long max_iteration = (long long)n * (long long)n / 2;
        for (long long i = 0; i < max_iteration; ++i) {
            int i_index = get_randindex(n), j_index = get_randindex(n);
            int gain = swap_gain(i_index, j_index, n);
            if (gain < 0) {
                update_state(i_index, j_index, n);
                curr += gain;
                restart = false;
                break;
            }
        }
    }
    clock_t end = clock();
    cout << "solved in " << (double)(end - start) / CLOCKS_PER_SEC << "s" << endl;
}

// 优化:更好地生成初始解(QS4算法)
void local_search_v2(int n, int c) {
    bool restart = true;
    int curr;
    clock_t start = clock();
    int m = n - c;
    while (true) {
        if (restart) curr = random_start_qs4(n, c);
        if (curr <= 0) break;
        restart = true;
        int gain = 0;
        // 随机交换两个皇后,第一个从产生冲突的里面选取,第二个完全随机选取
        for (int i = 0; i < n; ++i) {
            if (pos_diag[i - board[i] + n - 1] > 1 || neg_diag[i + board[i]] > 1) {
                for (int j = 0; j < n; ++j) {
                    if (i != j) {
                        gain = swap_gain(i, j, n);
                        if (gain < 0) {
                            update_state(i, j, n);
                            curr += gain;
                            restart = false;
                            break;
                        }
                    }
                }
                if (restart) break;
            }
        }
    }
    clock_t end = clock();
    cout << "solved in " << (double)(end - start) / CLOCKS_PER_SEC << "s" << endl;
}

int main() {
    srand(unsigned(time(0)));
    int n;
    while (true) {
        cout << "input (pos)num of queens, -1 to quit" << endl;
        cin >> n;
        if (n <= 0 || n > MAX) break;
        for (int run = 0; run < 10; run++) {
            for (int i = 0; i < n; ++i) board[i] = i;
            local_search_v2(n, get_c(n));
            // n很小时打印出结果,验证正确性
            if (n <= 10) {
                for (int i = 0; i < n; ++i) {
                    for (int j = 0; j < n; ++j) {
                        if (board[i] == j) cout << "Q";
                        else cout << "+";
                    }
                    cout << endl;
                }
            }
        }
    }
    return 0;
}