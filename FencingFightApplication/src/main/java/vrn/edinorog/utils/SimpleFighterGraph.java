package vrn.edinorog.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.MutablePair;
import vrn.edinorog.domain.Fighter;

import java.util.*;

public class SimpleFighterGraph {

    List<Node> nodes;
    int countFighterFromAnotherGroup;
    boolean playOff;

    public SimpleFighterGraph(List<Fighter> fighters, List<Fighter> fightersFromAnotherGroup, boolean playOff) {
        if (fightersFromAnotherGroup == null) {
            fightersFromAnotherGroup = new ArrayList<>();
        }
        this.playOff = playOff;
        countFighterFromAnotherGroup = fightersFromAnotherGroup.size();
        nodes = new ArrayList<>(fighters.size() + fightersFromAnotherGroup.size());

        for (Fighter fighter : fightersFromAnotherGroup) {
            nodes.add(new Node(fighter, new ArrayList<>(), 0));
        }

        for (Fighter fighter : fighters) {
            nodes.add(new Node(fighter, new ArrayList<>(), 0));
        }

        if (playOff) {
            fillPlayOffEdgeMatrix();
        } else {
            fillEdgeMatrix();
        }
    }

    public List<MutablePair<Fighter, Fighter>> getFighterPairs() {
        if (playOff) {
            return getPlayOffStageFighterPairs();
        } else {
            return getQualifyingStageFighterPairs();
        }
    }

    private List<MutablePair<Fighter, Fighter>> getQualifyingStageFighterPairs() {
        List<MutablePair<Fighter, Fighter>> fighterPairs = new ArrayList<>();

        if (countFighterFromAnotherGroup > 0) {
            List<Node> tempNodeList = nodes.subList(0, countFighterFromAnotherGroup);
            Collections.sort(tempNodeList, Comparator.comparingInt(Node::getSumWeights));
            tempNodeList.addAll(nodes.subList(countFighterFromAnotherGroup, nodes.size()));
            nodes = tempNodeList;
            tempNodeList = null;
            fillEdgeMatrix();

            Set<Integer> usedNodes = new HashSet<>();
            fighterPairs.addAll(getFighterPairs(nodes.subList(0, countFighterFromAnotherGroup), usedNodes));
            List<Integer> usedNodesList = new ArrayList<>(usedNodes);
            Collections.sort(usedNodesList);
            Collections.reverse(usedNodesList);

            for (int ind = 0; ind < usedNodesList.size(); ind++) {
                nodes.remove(usedNodesList.get(ind).intValue());
            }

            fillEdgeMatrix();
        }

        Collections.sort(nodes, Comparator.comparingInt(Node::getSumWeights));
        fillEdgeMatrix();
        fighterPairs.addAll(getFighterPairs(nodes, new HashSet<>()));
        return fighterPairs;
    }

    private List<MutablePair<Fighter, Fighter>> getPlayOffStageFighterPairs() {
        List<MutablePair<Fighter, Fighter>> fighterPairs = new ArrayList<>();
        Collections.sort(nodes, Comparator.comparingInt(Node::getSumWeights));
        fillPlayOffEdgeMatrix();

        for (int rowInd = 0; rowInd < nodes.size() / 2; rowInd++) {
            if (nodes.get(rowInd).getSumWeights() == 0) {
                throw new RuntimeException("Pair for fighter was found! Fighter id " + nodes.get(rowInd).getNodeValue().getId());
            } else {
                int rivalInd = -1;
                List<Integer> row = nodes.get(rowInd).getEdgeWeights();
                for (int colInd = nodes.size() / 2; colInd < row.size(); colInd++) {
                    if (row.get(colInd) == 2) {
                        rivalInd = colInd;
                        break;
                    } else if (row.get(colInd) == 1 && rivalInd == -1) {
                        rivalInd = colInd;
                    }
                }

                if (rivalInd == -1) {
                    throw new RuntimeException("Rival index = -1!");
                }

                fighterPairs.add(new MutablePair<>(nodes.get(rowInd).getNodeValue(), this.nodes.get(rivalInd).getNodeValue()));
                updateColumn(rivalInd);
            }

            updateColumn(rowInd);
        }

        return fighterPairs;
    }

    private List<MutablePair<Fighter, Fighter>> getFighterPairs(List<Node> nodes, Set<Integer> usedNodes) {

        List<MutablePair<Fighter, Fighter>> fighterPairs = new ArrayList<>();
        for (int rowInd = 0; rowInd < nodes.size(); rowInd++) {
            if (usedNodes.contains(rowInd)) {
                continue;
            }

            if (nodes.get(rowInd).getSumWeights() == 0) {
                fighterPairs.add(new MutablePair<>(nodes.get(rowInd).getNodeValue(), null));
            } else {
                int rivalInd = -1;
                List<Integer> row = nodes.get(rowInd).getEdgeWeights();
                for (int colInd = 0; colInd < row.size(); colInd++) {
                    if (row.get(colInd) == 2) {
                        rivalInd = colInd;
                        break;
                    } else if (row.get(colInd) == 1) {
                        rivalInd = colInd;
                    }
                }

                if (rivalInd == -1) {
                    throw new RuntimeException("Rival index = -1!");
                }

                if (usedNodes.contains(rivalInd)) {
                    throw new RuntimeException("Fighter already used!");
                }

                fighterPairs.add(new MutablePair<>(nodes.get(rowInd).getNodeValue(), this.nodes.get(rivalInd).getNodeValue()));
                usedNodes.add(rivalInd);
                updateColumn(rivalInd);
            }

            usedNodes.add(rowInd);
            updateColumn(rowInd);
        }
        return fighterPairs;
    }

    private void fillEdgeMatrix() {
        for (int row = 0; row < nodes.size(); row++) {
            nodes.get(row).setEdgeWeights(new ArrayList<>());
            nodes.get(row).setSumWeights(0);
            for(int col = 0; col < nodes.size(); col++) {
                if(nodes.get(row).getNodeValue().getId().equals(nodes.get(col).getNodeValue().getId())) {
                    nodes.get(row).getEdgeWeights().add(0);
                } else if (nodes.get(row).getNodeValue().getRivalIds().contains(nodes.get(col).getNodeValue().getId())) {
                    nodes.get(row).getEdgeWeights().add(0);
                } else if (nodes.get(row).getNodeValue().getFighterIdFromTheSameClub().contains(nodes.get(col).getNodeValue().getId())) {
                    nodes.get(row).getEdgeWeights().add(1);
                    nodes.get(row).incSumWeights(1);
                } else {
                    nodes.get(row).getEdgeWeights().add(2);
                    nodes.get(row).incSumWeights(2);
                }
            }
        }
    }

    private void fillPlayOffEdgeMatrix() {
        for (int row = 0; row < nodes.size() / 2; row++) {
            nodes.get(row).setEdgeWeights(new ArrayList<>());
            nodes.get(row).setSumWeights(0);
            for(int col = 0; col < nodes.size(); col++) {
                if(nodes.get(row).getNodeValue().getId().equals(nodes.get(col).getNodeValue().getId())) {
                    nodes.get(row).getEdgeWeights().add(0);
                } else if (col < nodes.size() / 2) {
                    nodes.get(row).getEdgeWeights().add(0);
                } else if (nodes.get(row).getNodeValue().getFighterIdFromTheSameClub().contains(nodes.get(col).getNodeValue().getId())) {
                    nodes.get(row).getEdgeWeights().add(1);
                    nodes.get(row).incSumWeights(1);
                } else {
                    nodes.get(row).getEdgeWeights().add(2);
                    nodes.get(row).incSumWeights(2);
                }
            }
        }
        for (int row = nodes.size() / 2; row < nodes.size(); row++) {
            nodes.get(row).setEdgeWeights(new ArrayList<>());
            nodes.get(row).setSumWeights(nodes.size() * 2);
        }
    }

    private void updateColumn(int columnNumber) {
        for (Node node : nodes) {
            node.decSumWeights(node.getEdgeWeights().get(columnNumber));
            node.getEdgeWeights().set(columnNumber, 0);
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter @Getter
    private static class Node {
        private Fighter nodeValue;
        private List<Integer> edgeWeights;
        private int sumWeights = 0;

        public void incSumWeights(int value) {
            sumWeights += value;
        }

        public void decSumWeights(int value) {
            sumWeights -= value;
        }
    }

}