import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

// Classe que representa uma pessoa na árvore genealógica
class Pessoa {
    String nome;
    Pessoa pai;
    Pessoa filho1;
    Pessoa filho2;

    // Construtor inicializa com nome e sem parentes
    Pessoa(String nome) {
        this.nome = nome;
        this.pai = null;
        this.filho1 = null;
        this.filho2 = null;
    }

    // Adiciona filhos (máximo de 2 por pessoa)
    void adicionarFilho(Pessoa f) {
        if (filho1 == null) {
            filho1 = f;
        } else if (filho2 == null) {
            filho2 = f;
        }
    }
}

public class ArvoreFamiliaCompleta {

    static Pessoa[] pessoas = new Pessoa[200]; // armazenamento das pessoas
    static int qtd = 0; // contador de pessoas já criadas

    // Busca pessoa pelo nome
    static Pessoa buscar(String nome) {
        for (int i = 0; i < qtd; i++) {
            if (pessoas[i].nome.equals(nome)) return pessoas[i];
        }
        return null;
    }

    // Recupera pessoa existente ou cria uma nova
    static Pessoa getPessoa(String nome) {
        Pessoa p = buscar(nome);
        if (p == null) {
            p = new Pessoa(nome);
            pessoas[qtd++] = p;
        }
        return p;
    }

    // Distância em arestas de 'from' até 'to' subindo pela linha paterna
    static int distancia(Pessoa from, Pessoa to) {
        int d = 0;
        Pessoa cur = from;
        while (cur != null) {
            if (cur == to) return d;
            cur = cur.pai;
            d++;
        }
        return -1;
    }

    // Formata descendentes (filho, neto, bisneto, tataraneto...)
    static String formatarDescendente(int nivel) {
        if (nivel == 1) return "filho";
        if (nivel == 2) return "neto";
        if (nivel == 3) return "bisneto";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nivel - 3; i++) sb.append("tatar");
        sb.append("aneto");
        return sb.toString();
    }

    // Formata ancestrais (pai, avô, bisavô, tataravô...)
    static String formatarAncestral(int nivel) {
        if (nivel == 1) return "pai";
        if (nivel == 2) return "avô";
        if (nivel == 3) return "bisavô";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nivel - 3; i++) sb.append("tatar");
        sb.append("avô");
        return sb.toString();
    }

    // Verifica se duas pessoas são irmãos
    static boolean irmaos(Pessoa p, Pessoa q) {
        return (p != null && q != null && p.pai != null && p.pai == q.pai);
    }

    // Lista todos os ancestrais de uma pessoa junto com seus níveis
    static Pessoa[] getAncestrais(Pessoa p, int[] niveis) {
        Pessoa[] lista = new Pessoa[200];
        int idx = 0;
        int nivel = 0;
        Pessoa cur = p;
        while (cur != null) {
            lista[idx] = cur;
            niveis[idx] = nivel;
            cur = cur.pai;
            idx++;
            nivel++;
        }
        lista[idx] = null;
        return lista;
    }

    // Calcula se são primos (primo-k em grau m)
    static String primos(Pessoa p, Pessoa q) {
        int[] niveisP = new int[200];
        int[] niveisQ = new int[200];
        Pessoa[] ancP = getAncestrais(p, niveisP);
        Pessoa[] ancQ = getAncestrais(q, niveisQ);

        Pessoa ancestral = null;
        int distP = -1, distQ = -1;
        int melhorSoma = Integer.MAX_VALUE;

        // procura ancestral comum mais próximo
        for (int i = 0; ancP[i] != null; i++) {
            for (int j = 0; ancQ[j] != null; j++) {
                if (ancP[i] == ancQ[j]) {
                    int soma = niveisP[i] + niveisQ[j];
                    if (soma < melhorSoma) {
                        melhorSoma = soma;
                        ancestral = ancP[i];
                        distP = niveisP[i];
                        distQ = niveisQ[j];
                    }
                }
            }
        }

        if (ancestral == null) return "sem relacao";

        int degree = Math.min(distP, distQ) - 1; // grau de primo
        int removal = Math.abs(distP - distQ);   // diferença de gerações

        if (degree == 0 && removal == 0) return "irmao";
        return "primo-" + degree + " em grau " + removal;
    }

    // Determina o parentesco entre duas pessoas
    static String parentesco(Pessoa p, Pessoa q) {
        // p descendente de q?
        int dDesc = distancia(p, q);
        if (dDesc >= 1) return formatarDescendente(dDesc);

        // p ancestral de q?
        int dAnc = distancia(q, p);
        if (dAnc >= 1) return formatarAncestral(dAnc);

        // irmãos?
        if (irmaos(p, q)) return "irmao";

        // primos (inclui primo-0 em grau m)
        return primos(p, q);
    }

    // Programa principal: primeiro lê relações pai-filho,
    // depois consultas de parentesco
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String linha;
        boolean lendoRelacoes = true;

        while ((linha = br.readLine()) != null) {
            linha = linha.trim();
            if (linha.equals("")) {
                lendoRelacoes = false; // linha em branco marca fim das relações
                continue;
            }
            String[] partes = linha.split("\\s+");
            if (partes.length < 2) continue;

            if (lendoRelacoes) {
                // leitura de relação "filho pai"
                String filho = partes[0];
                String pai = partes[1];
                Pessoa f = getPessoa(filho);
                Pessoa p = getPessoa(pai);
                f.pai = p;
                p.adicionarFilho(f);
            } else {
                // consulta de parentesco "pessoa1 pessoa2"
                Pessoa pa = getPessoa(partes[0]);
                Pessoa pb = getPessoa(partes[1]);
                System.out.println(parentesco(pa, pb));
            }
        }
    }
}
