#ifndef NODE_HPP
#define NODE_HPP

#include <vector>
#include <string>
using std::vector;
using std::string;
//采用结构体来保存八数码问题的搜索状态，即一个节点
//采用string来保存一个八数码状态，方便比较两个八数码状态是否相等

struct Node {
    string digital;                 //八数码状态
    Node* parent;                   //父节点
    vector<string> precursor;       //所有前驱节点
    vector<Node*> successor;        //所有后继节点
    int depth;                      //节点深度，即g(n)
    int eval;                       //节点的评估，视不同评估函数而定，即h(n)
    Node(string digital, Node* parent, vector<string> precursor, vector<Node*> successor, int depth, int eval) {
        this->digital = digital;
        this->parent = parent;
        this->precursor = precursor;
        this->successor = successor;
        this->depth = depth;
        this->eval = eval;
    }

    //判断输入的八数码状态是否是前驱节点
    bool isPrecursor(string digital) {
        for (auto it : precursor) {
            if (it == digital) return true;
        }
        return false;
    }
};

//节点指针的比较运算
bool cmp (const Node* node1, const Node* node2) {
    return (node1->depth + node1->eval) < (node2->depth + node2->eval);
}

#endif