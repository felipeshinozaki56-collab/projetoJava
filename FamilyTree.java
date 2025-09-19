import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Pessoa {
    private String nome;
    private Pessoa pai;
    private List<Pessoa> filhos;

    public Pessoa(String nome) {
        this.nome = nome;
        this.filhos = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public Pessoa getPai() {
        return pai;
    }

    public void setPai(Pessoa pai) {
        this.pai = pai;
    }

    public List<Pessoa> getFilhos() {
        return filhos;
    }

    public void adicionarFilho(Pessoa filho) {
        this.filhos.add(filho);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pessoa pessoa = (Pessoa) obj;
        return nome.equals(pessoa.nome);
    }

    @Override
    public int hashCode() {
        return nome.hashCode();
    }
}

public class FamilyTree {
    private static Map<String, Pessoa> pessoas = new HashMap<>();

    public static Pessoa getPessoa(String nome) {
        if (!pessoas.containsKey(nome)) {
            pessoas.put(nome, new Pessoa(nome));
        }
        return pessoas.get(nome);
    }

    public static void construirArvore(List<String> linhasRelacoes) {
        for (String linha : linhasRelacoes) {
            if (linha.trim().isEmpty()) {
                continue;
            }
            String[] partes = linha.split(" ");
            if (partes.length < 2) {
                continue;
            }
            
            String filhoNome = partes[0];
            String paiNome = partes[1];
            
            Pessoa filho = getPessoa(filhoNome);
            Pessoa pai = getPessoa(paiNome);
            
            filho.setPai(pai);
            pai.adicionarFilho(filho);
        }
    }

    public static int descendenteDe(Pessoa p, Pessoa q) {
        if (p.equals(q)) {
            return -1; // Uma pessoa não é descendente de si mesma
        }

        Pessoa current = p;
        int nivel = 0;
        while (current.getPai() != null) {
            if (current.getPai().equals(q)) {
                return nivel;
            }
            current = current.getPai();
            nivel++;
        }
        return -1;
    }

    public static int ancestralDe(Pessoa p, Pessoa q) {
        if (p.equals(q)) {
            return -1; // Uma pessoa não é ancestral de si mesma
        }

        Pessoa current = q;
        int nivel = 0;
        while (current.getPai() != null) {
            if (current.getPai().equals(p)) {
                return nivel;
            }
            current = current.getPai();
            nivel++;
        }
        return -1;
    }

    public static boolean irmaos(Pessoa p, Pessoa q) {
        return p.getPai() != null && p.getPai().equals(q.getPai());
    }

    public static List<Map.Entry<Pessoa, Integer>> getAncestraisComNiveis(Pessoa p) {
        List<Map.Entry<Pessoa, Integer>> ancestrais = new ArrayList<>();
        Pessoa current = p;
        int nivel = 0;
        while (current != null) {
            ancestrais.add(new HashMap.SimpleEntry<>(current, nivel));
            current = current.getPai();
            nivel++;
        }
        return ancestrais;
    }

    public static String primos(Pessoa p, Pessoa q) {
        List<Map.Entry<Pessoa, Integer>> ancP = getAncestraisComNiveis(p);
        List<Map.Entry<Pessoa, Integer>> ancQ = getAncestraisComNiveis(q);

        Pessoa ancestralComum = null;
        int distPAC = -1;
        int distQAC = -1;
        
        for (Map.Entry<Pessoa, Integer> entryP : ancP) {
            Pessoa ap = entryP.getKey();
            int np = entryP.getValue();
            for (Map.Entry<Pessoa, Integer> entryQ : ancQ) {
                Pessoa aq = entryQ.getKey();
                int nq = entryQ.getValue();
                if (ap.equals(aq)) {
                    if (ancestralComum == null || (np + nq) < (distPAC + distQAC)) {
                        ancestralComum = ap;
                        distPAC = np;
                        distQAC = nq;
                    }
                }
            }
        }

        if (ancestralComum == null) {
            return "sem relacao";
        }

        // Se um é ancestral do outro, não são primos no sentido estrito do problema
        if (p.equals(ancestralComum) || q.equals(ancestralComum)) {
            return "sem relacao";
        }

        int k = Math.min(distPAC, distQAC);
        int m = Math.abs(distPAC - distQAC);

        if (k == 0 && m == 0) {
            return "sem relacao";
        } else if (k == 0) {
            return String.format("primo-0 em grau %d", m);
        }

        return String.format("primo-%d em grau %d", k, m);
    }

    public static String formatarDescendente(int nivel) {
        if (nivel == 0) return "filho";
        if (nivel == 1) return "neto";
        if (nivel == 2) return "bisneto";
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < nivel - 2; i++) s.append("tata");
        return s.toString() + "raneto";
    }

    public static String formatarAncestral(int nivel) {
        if (nivel == 0) return "pai";
        if (nivel == 1) return "avô";
        if (nivel == 2) return "bisavô";
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < nivel - 2; i++) s.append("tata");
        return s.toString() + "ravô";
    }

    public static String parentesco(Pessoa p, Pessoa q) {
        if (p.equals(q)) return "sem relacao";

        int d1 = descendenteDe(p, q);
        if (d1 >= 0) {
            return formatarDescendente(d1);
        }

        int d2 = ancestralDe(p, q);
        if (d2 >= 0) {
            return formatarAncestral(d2);
        }

        if (irmaos(p, q)) {
            return "irmao";
        }

        String primoRel = primos(p, q);
        if (!primoRel.equals("sem relacao")) {
            return primoRel;
        }

        return "sem relacao";
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> relacoes = new ArrayList<>();
        List<String> consultas = new ArrayList<>();
        
        boolean lendoRelacoes = true;
        while (scanner.hasNextLine()) {
            String linha = scanner.nextLine().trim();
            if (linha.isEmpty()) {
                lendoRelacoes = false;
                continue;
            }
            
            if (lendoRelacoes) {
                relacoes.add(linha);
            } else {
                consultas.add(linha);
            }
        }
        scanner.close();

        construirArvore(relacoes);

        for (String consultaStr : consultas) {
            String[] partes = consultaStr.split(" ");
            if (partes.length < 2) {
                continue;
            }
            
            String pessoa1Nome = partes[0];
            String pessoa2Nome = partes[1];
            
            Pessoa p1 = getPessoa(pessoa1Nome);
            Pessoa p2 = getPessoa(pessoa2Nome);
            
            System.out.println(parentesco(p1, p2));
        }
    }
}
