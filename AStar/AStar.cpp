#include "AStar.hpp"
#include <iostream>
#include <cmath>
#include <algorithm>
using std::cout;
using std::endl;

//判断八数码问题是否有解
/* 一个状态表示成一维的形式，求出除0之外所有数字的逆序数之和，
也就是每个数字前面比它大的数字的个数的和，称为这个状态的逆序。

若两个状态的逆序奇偶性 相同，则可相互到达，否则不可相互到达。 
*/

bool AStar::solvable(string digital) {
    int count1 = 0, count2 = 0;
    for (int i = 0; i < 9; ++i) {
        for (int j = 0; j < i; ++j) {
            if (digital[i] != '0' && digital[j] > digital[i]) 
                count1++;
            if (goal[i] != '0' && goal[j] > goal[i])
                count2++;
        }
    }
    return (count1 % 2 == count2 % 2);
}

//输出矩阵形式的节点的八数码状态
void AStar::print(string digital) {
    for (int i = 0; i < 9; ++i) {
        if (i % 3 == 0) cout << endl; 
        cout << digital[i] << " ";
    }
    cout << endl;
}

//评估函数1，以放错位置的数字个数为估计
int AStar::evaluation1(string digital) {
    int eval = 0;
    //估计放错位置的数字个数
    for (int i = 0; i < 9; ++i) {
        if (digital[i] != goal[i]) eval++;
    }
    return eval;
}

//评估函数2，以放错位置的数字移动到正确位置所需步数为估计
//方法为计算横、纵坐标之差的绝对值的和
int AStar::evaluation2(string digital) {
    int eval = 0;
    for (int i = 0; i < 9; ++i) {
        if (digital[i] != goal[i]) {
            for (int j = 0; j < 9; ++j) {
                if (digital[i] == goal[j]) {
                    //x为横坐标，y为纵坐标
                    int x_i = i % 3, y_i = i / 3;
                    int x_j = j % 3, y_j = j / 3;
                    eval += abs(x_i - x_j) + abs(y_i - y_j);
                }
            }
        }
    }
    return eval;
}

//产生下一步所有可能的八数码状态(一切后继)
vector<string> AStar::generateAllSuccessor(string digital) {
    vector<string> successors = {};
    int index = digital.find('0');
    //得到0的横坐标和纵坐标
    int x = index % 3, y = index / 3;
    //0上移
    if (y != 0) {
        string result = digital;
        char tmp = result[index];
        result[index] = result[index - 3];
        result[index - 3] = tmp;
        successors.push_back(result);
    }
    //0下移
    if (y != 2) {
        string result = digital;
        char tmp = result[index];
        result[index] = result[index + 3];
        result[index + 3] = tmp;
        successors.push_back(result);
    }
    //0左移
    if (x != 0) {
        string result = digital;
        char tmp = result[index];
        result[index] = result[index - 1];
        result[index - 1] = tmp;
        successors.push_back(result);
    }
    //0右移
    if (x != 2) {
        string result = digital;
        char tmp = result[index];
        result[index] = result[index + 1];
        result[index + 1] = tmp;
        successors.push_back(result);
    }
    return successors;
}

//递归更新子节点深度
void AStar::update(vector<Node*> successor, int depth) {
    for (auto i : successor) {
        if (i->depth > depth) {
            i->depth = depth;
            update(i->successor, depth);
        }
    }
}

//回溯得到成功的路径
stack<string> AStar::getPath(Node* n) {
    stack<string> path = {};
    while (n != NULL) {
        path.push(n->digital);
        n = n->parent;
    }
    return path;
}

int AStar::evaluation(int select, string digital) {
    return (select == 1) ? evaluation1(digital) : evaluation2(digital);
}

//参数表示选择哪种评估函数
void AStar::AStarSearch(int select) {
    cout << "初始八数码状态:";
    print(start);
    //首先判断是否有解
    if (solvable(start) == false) {
        cout << "No Solution" << endl;
        exit(1);
    }
    //open表保存尚未考察过的节点
    //close表保存已经考察过的节点
    //搜索图由这两张表组成
    vector<Node*> open = {}, close = {};

    //生成初始状态
    Node* S0 = new Node(start, NULL, {}, {}, 1, evaluation(select, start));
    open.push_back(S0);

    //存放成功的路径的栈
    stack<string> path;

    while (true) {
        //失败退出
        if (open.empty()) {
            cout << "搜索失败" << endl;
            exit(1);
        }

        //在open表上取f值最小的节点n
        //n放入close表，并从open表移除
        sort(open.begin(), open.end(), cmp);
        Node* n = open[0];
        open.erase(open.begin());
        close.push_back(n);
        
        //若n是目标状态，则成功退出
        if (n->digital == goal) {
            cout << "搜索成功" << endl;
            //从n开始回溯得到路径
            path = getPath(n);
            break;
        }

        //产生n的一切后继
        vector<string> successors = generateAllSuccessor(n->digital);
        //后继的前驱节点
        vector<string> precursor = n->precursor;
        precursor.push_back(n->digital);
        for (auto successor : successors) {
            //对不是n的前驱节点的后继进行操作
            if (n->isPrecursor(successor) == false) {
                bool isInGraph = false;
                //在open表中寻找该后继
                for (auto it : open) {
                    //后继在open表中
                    if (it->digital == successor) {
                        //当前路径较好，修改后继的指针，使其指向n
                        if (n->depth + 1 < it->depth) {
                            //从后继原来父节点的后继列表中删去这个后继
                            for (auto i = it->parent->successor.begin(); i != it->parent->successor.end(); ++i) {
                                if ((*i)->digital == successor) {
                                    it->parent->successor.erase(i);
                                    break;
                                }
                            }
                            //修改后继的父节点指针
                            it->parent = n;
                            it->depth = n->depth + 1;
                            it->precursor = precursor;
                        }
                        isInGraph = true;
                        break;
                    }
                }
                //在close表中寻找该后继
                for (auto it : close) {
                    //后继在close表中
                    if (it->digital == successor) {
                        //当前路径较好，修改后继的指针，使其指向n
                        if (n->depth + 1 < it->depth) {
                            //从后继原来父节点的后继列表中删去这个后继
                            for (auto i = it->parent->successor.begin(); i != it->parent->successor.end(); ++i) {
                                if ((*i)->digital == successor) {
                                    it->parent->successor.erase(i);
                                    break;
                                }
                            }
                            //修改后继的父节点指针
                            it->parent = n;
                            it->depth = n->depth + 1;
                            it->precursor = precursor;
                            //更改其子节点的指针及费用
                            update(it->successor, n->depth + 1);
                        }
                        isInGraph = true;
                        break;
                    }
                }
                //后继不在open表也不在close表
                if (isInGraph == false) {
                    Node* child = new Node(successor, n, precursor, {}, n->depth + 1, evaluation(select, successor));
                    n->successor.push_back(child);
                    open.push_back(child);
                }
            }
        }
    }

    //输出成功的路径
    while (!path.empty()) {
        print(path.top());
        path.pop();
    }
}