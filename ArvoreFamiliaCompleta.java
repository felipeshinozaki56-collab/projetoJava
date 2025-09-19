import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

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

    static Pessoa[] pessoas = new Pessoa[200];
    static int qtd = 0;

    static Pessoa buscar(String nome) {
        for (int i = 0; i < qtd; i++) {
            if (pessoas[i].nome.equals(nome)) return pessoas[i];
        }
        return null;
    }

    static Pessoa getPessoa(String nome) {
        Pessoa p = buscar(nome);
        if (p == null) {
            p = new Pessoa(nome);
            pessoas[qtd++] = p;
        }
        return p;
    }

    // distância em arestas de 'from' até 'to' (0 = mesma pessoa, 1 = pai imediato, 2 = avô, ...)
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

    // Formata descendente: 1=filho,2=neto,3=bisneto,4=tataraneto,...
    static String formatarDescendente(int nivel) {
        if (nivel == 1) return "filho";
        if (nivel == 2) return "neto";
        if (nivel == 3) return "bisneto";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nivel - 3; i++) sb.append("tatar");
        sb.append("aneto"); // tataraneto, tatartataraneto, ...
        return sb.toString();
    }

    // Formata ancestral: 1=pai,2=avô,3=bisavô,4=tataravô,...
    static String formatarAncestral(int nivel) {
        if (nivel == 1) return "pai";
        if (nivel == 2) return "avô";
        if (nivel == 3) return "bisavô";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nivel - 3; i++) sb.append("tatar");
        sb.append("avô");
        return sb.toString();
    }

    static boolean irmaos(Pessoa p, Pessoa q) {
        return (p != null && q != null && p.pai != null && p.pai == q.pai);
    }

    // retorna ancestrais com níveis (0=self,1=parent,2=avô,...). niveis[i] corresponde ao anc[i]
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

    // calcula primos no formato "primo-k em grau m" ou "irmao" caso sejam irmãos
    static String primos(Pessoa p, Pessoa q) {
        int[] niveisP = new int[200];
        int[] niveisQ = new int[200];
        Pessoa[] ancP = getAncestrais(p, niveisP);
        Pessoa[] ancQ = getAncestrais(q, niveisQ);

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

        int degree = Math.min(distP, distQ) - 1; // primo-k, k = min(distP,distQ)-1
        int removal = Math.abs(distP - distQ);   // em grau m

        // se degree == 0 e removal == 0 => irmãos (mas já checado antes)
        if (degree == 0 && removal == 0) return "irmao";

        return "primo-" + degree + " em grau " + removal;
    }

    static String parentesco(Pessoa p, Pessoa q) {
        // p descendente de q?
        int dDesc = distancia(p, q);
        if (dDesc >= 1) return formatarDescendente(dDesc);

        // p ancestral de q?
        int dAnc = distancia(q, p);
        if (dAnc >= 1) return formatarAncestral(dAnc);

        // irmãos?
        if (irmaos(p, q)) return "irmao";

        // primos (inclui os casos "primo-0 em grau m")
        return primos(p, q);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String linha;
        boolean lendoRelacoes = true;

        while ((linha = br.readLine()) != null) {
            linha = linha.trim();
            if (linha.equals("")) {
                lendoRelacoes = false;
                continue;
            }
            String[] partes = linha.split("\\s+");
            if (partes.length < 2) continue;

            if (lendoRelacoes) {
                String filho = partes[0];
                String pai = partes[1];
                Pessoa f = getPessoa(filho);
                Pessoa p = getPessoa(pai);
                f.pai = p;
                p.adicionarFilho(f);
            } else {
                Pessoa pa = getPessoa(partes[0]);
                Pessoa pb = getPessoa(partes[1]);
                System.out.println(parentesco(pa, pb));
            }
        }
    }
}
