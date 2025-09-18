import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

// Aluno: Felipe Hideki Rodrigues Shinozaki
//RA: 10438584
class Pessoa {
    String nome;
    Pessoa pai;
    Pessoa filho1;
    Pessoa filho2;

    Pessoa(String nome) {
        this.nome = nome;
        this.pai = null;
        this.filho1 = null;
        this.filho2 = null;
    }

    void adicionarFilho(Pessoa f) {
        if (filho1 == null) {
            filho1 = f;
        } else if (filho2 == null) {
            filho2 = f;
        }
    }
}

public class ArvoreFamiliaCompleta {

    static Pessoa[] pessoas = new Pessoa[200]; // até 200 pessoas
    static int qtd = 0;

    // Busca pessoa pelo nome
    static Pessoa buscar(String nome) {
        for (int i = 0; i < qtd; i++) {
            if (pessoas[i].nome.equals(nome)) {
                return pessoas[i];
            }
        }
        return null;
    }

    // Cria ou retorna pessoa
    static Pessoa getPessoa(String nome) {
        Pessoa p = buscar(nome);
        if (p == null) {
            p = new Pessoa(nome);
            pessoas[qtd++] = p;
        }
        return p;
    }

    // Verifica se p é descendente de q e retorna nível (0=filho direto)
    static int descendenteDe(Pessoa p, Pessoa q) {
        int nivel = 0;
        while (p != null) {
            if (p == q) return nivel;
            p = p.pai;
            nivel++;
        }
        return -1;
    }

    // Verifica se p é ancestral de q
    static int ancestralDe(Pessoa p, Pessoa q) {
        return descendenteDe(q, p);
    }

    // Formata descendente
    static String formatarDescendente(int nivel) {
        if (nivel == 0) return "filho";
        if (nivel == 1) return "neto";
        if (nivel == 2) return "bisneto";
        String s = "";
        for (int i = 0; i < nivel - 2; i++) s += "tatar";
        return s + "aneto";
    }

    // Formata ancestral
    static String formatarAncestral(int nivel) {
        if (nivel == 0) return "pai";
        if (nivel == 1) return "avô";
        if (nivel == 2) return "bisavô";
        String s = "";
        for (int i = 0; i < nivel - 2; i++) s += "tatar";
        return s + "avô";
    }

    // Testa irmãos
    static boolean irmaos(Pessoa p, Pessoa q) {
        return (p.pai != null && p.pai == q.pai);
    }

    // Encontra ancestrais de uma pessoa até a raiz
    static Pessoa[] getAncestrais(Pessoa p, int[] niveis) {
        Pessoa[] lista = new Pessoa[200];
        int idx = 0;
        int nivel = 0;
        while (p != null) {
            lista[idx] = p;
            niveis[idx] = nivel;
            p = p.pai;
            idx++;
            nivel++;
        }
        lista[idx] = null;
        return lista;
    }

    // Calcula se são primos
    static String primos(Pessoa p, Pessoa q) {
        int[] niveisP = new int[200];
        int[] niveisQ = new int[200];

        Pessoa[] ancP = getAncestrais(p, niveisP);
        Pessoa[] ancQ = getAncestrais(q, niveisQ);

        // Encontra o ancestral comum mais próximo
        Pessoa ancestral = null;
        int distP = -1, distQ = -1;
        int melhorSoma = Integer.MAX_VALUE;

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

        // Calcula grau dos primos
        int k = Math.min(distP, distQ);
        int m = Math.abs(distP - distQ);

        if (k == 0) return "irmao"; // primo-0 = irmãos

        return "primo-" + k + " em grau " + m;
    }

    // Calcula parentesco geral
    static String parentesco(Pessoa p, Pessoa q) {
        int d1 = descendenteDe(p, q);
        if (d1 >= 0) return formatarDescendente(d1);

        int d2 = ancestralDe(p, q);
        if (d2 >= 0) return formatarAncestral(d2);

        if (irmaos(p, q)) return "irmao";

        return primos(p, q);
    }

    // Programa principal
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String linha;
        boolean lendoRelacoes = true;

        // Primeiro: construir árvore
        while ((linha = br.readLine()) != null) {
            linha = linha.trim();
            if (linha.equals("")) {
                lendoRelacoes = false;
                continue;
            }
            String[] partes = linha.split(" ");
            if (partes.length < 2) continue;

            if (lendoRelacoes) {
                String filho = partes[0];
                String pai = partes[1];
                Pessoa f = getPessoa(filho);
                Pessoa p = getPessoa(pai);
                f.pai = p;
                p.adicionarFilho(f);
            } else {
                String a = partes[0];
                String b = partes[1];
                Pessoa pa = getPessoa(a);
                Pessoa pb = getPessoa(b);
                System.out.println(parentesco(pa, pb));
            }
        }
    }
}
