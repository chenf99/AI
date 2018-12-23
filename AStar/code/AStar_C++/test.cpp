#include "AStar.hpp"
#include "AStar.cpp"

int main() {
    AStar astar;
    astar.AStarSearch(2);
    cout << astar.evaluation1("283164705") << " " << astar.evaluation2("123856074");
}