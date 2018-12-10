#ifndef ASTAR_HPP
#define ASTAR_HPP

#include "node.hpp"
#include <stack>
using std::stack;

class AStar {
private: 
    string start;               //初始状态
    string goal;                //目标状态
public:
    AStar(string start = "283164705", string goal = "123804765") {
        this->start = start;
        this->goal = goal;
    }

    bool solvable(string digital);                          //判断八数码问题是否有解
    void print(string digital);                             //输出矩阵形式的节点的八数码状态
    int evaluation1(string digital);                        //评估函数1，以放错位置的数字个数为估计
    int evaluation2(string digital);                        //评估函数2，以放错位置的数字个数为估计
    vector<string> generateAllSuccessor(string digital);    //产生下一步所有可能的八数码状态(一切后继)
    void update(vector<Node*> successor, int depth);        //递归更新子节点深度
    stack<string> getPath(Node* n);                         //回溯得到成功的路径
    void AStarSearch();                                     //A*算法主函数
    
};

#endif