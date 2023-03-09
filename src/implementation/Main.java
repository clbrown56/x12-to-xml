package implementation;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class Main {
	static protected TreeMap<Integer, Character> loopCode1 = new TreeMap<Integer, Character>();
	static protected TreeMap<Integer, Character> loopCode2 = new TreeMap<Integer, Character>();
	static protected TreeMap<Integer, Character> loopCode3 = new TreeMap<Integer, Character>();
	static protected TreeMap<Character, Character> alphaConv = new TreeMap<Character, Character>();
	static protected boolean hasPrevious = false;
	static protected boolean activeLoop2 = false;
	static protected boolean activeLoop3 = false;
	static protected int level = 1;
	
	protected static void process(String filename) throws IOException, FileNotFoundException {
		initLoopCodes();    
		initChar();
		String file = "src/" + filename;
		File output = new File("output.txt");
		output.createNewFile();
		PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		int numTabs = 0;
		int idCounter = 1;
		String tempLast = "";
		String control = "";
		writer.println("                                                                 ,---,                                                  ,--,    ");
		writer.println("                                                              ,`--.' |                                         ____  ,---.'|    ");
		writer.println("  ,----..                                                     |   :  :                  ,--,     ,--,        ,'  , `.|   | :    ");
		writer.println(" /   /   \\                                                    |   |  '                  |'. \\   / .`|     ,-+-,.' - |:   : |    ");
		writer.println("|   :     :  ,---.        ,---,      ,---,    ,---.    __  ,-.'   :  |                  ; \\ `\\ /' / ;  ,-+-. ;   , |||   ' :    ");
		writer.println(".   |  ;. / '   ,'\\   ,-+-. /  | ,-+-. /  |  '   ,'\\ ,' ,'/ /|;   |.'.--.--.            `. \\  /  / .' ,--.'|'   |  ;|;   ; '    ");
		writer.println(".   ; /--` /   /   | ,--.'|'   |,--.'|'   | /   /   |'  | |' |'---' /  /    '            \\  \\/  / ./ |   |  ,', |  ':'   | |__  ");
		writer.println(";   | ;   .   ; ,. :|   |  ,\"' |   |  ,\"\' |.   ; ,. :|  |   ,\'     |  :  /`./             \\  \\.\'  /  |   | /  | |  |||   | :.\'| ");
		writer.println("|   : |   '   | |: :|   | /  | |   | /  | |'   | |: :'  :  /       |  :  ;_                \\  ;  ;   '   | :  | :  |,'   :    ; ");
		writer.println(".   | '___'   | .; :|   | |  | |   | |  | |'   | .; :|  | '         \\  \\    `.            / \\  \\  \\  ;   . |  ; |--' |   |  ./  ");
		writer.println("\'   ; : .\'|   :    ||   | |  |/|   | |  |/ |   :    |;  : |          `----.   \\          ;  /\\  \\  \\ |   : |  | ,    ;   : ;    ");
		writer.println("'   | '/  :\\   \\  / |   | |--' |   | |--'   \\   \\  / |  , ;         /  /`--'  /        ./__;  \\  ;  \\|   : '  |/     |   ,/     ");
		writer.println("|   :    /  `----'  |   |/     |   |/        `----'   ---'         '--'.     /         |   : / \\  \\  ;   | |`-\'      \'---\'      ");
		writer.println("\\   \\ .'           '---'      '---'                                 `--\'---'          ;   |/   \\  \' |   ;/                     ");
		writer.println("`---`                                                                                `---'     `--`'---'                      ");

		writer.println("<ConnorData>");
		while ((line = reader.readLine()) != null) { // iterates through the lines
			String[] values = line.split("\\*");
			int elementIndex = 1;
			String tag = values[0];
			if (tag.equals("ISA")) {
				// We need to figure out what all goes in Interchange tab
				generateIndent(++numTabs, writer);
				writer.println("<Interchange AckRequested=\"" + values[14] + "\" AuthorizationQual=\"" + values[1] + "\" ControlNumber=\"" + values[13] + "\" ElementDelimeter = \"" + values[11] + "\">");
				
				generateIndent(++numTabs, writer);
				writer.println("<SenderID Qualifier=\"" + values[5] + "\">" + values[6].trim() + "</SenderID>");
				
				generateIndent(numTabs, writer);
				writer.println("<RecieverID Qualifier=\"" + values[7] + "\">" + values[8].trim() + "</RecieverID>");
				
			} else if (values[0].equals("GS")) {
				
				generateIndent(numTabs++, writer);
				writer.println("<Group GroupType = \"" + values[1] + "\" ApplSender = \"" + values[2]+ "\" ApplSender = \"" + values[3] + "\" Date = \"" + values[4] + "\">");
				control = values[6];
				
			} else if (tag.equals("GE")) {
				
				generateIndent(--numTabs, writer);
				writer.println("</Group>");
				
			} else if (tag.equals("IEA")) {
				
				generateIndent(--numTabs, writer);
				writer.println("</Interchange>");
				
			} else if (tag.equals("SE")) {
				if (hasPrevious) {
					generateIndent(--numTabs, writer);
					writer.println("</" + generateLoopCode(1) + ">");
				}
				generateIndent(--numTabs, writer);
				restartLoopCodes();
				writer.println("</Transaction>");
				
			} else {
				if (tag.equals("ST")) {
					generateIndent(numTabs++, writer);
					tempLast = values[values.length-1];
					tempLast = tempLast.substring(0,tempLast.length()-1);
					writer.println("<Transaction ControlNumber = \"" + control + "\" DocType = \"" + values[1] + "\" SegIdx = \"" + idCounter + "\" Version = \"" + tempLast + "\">");
				}
				
				if (level == 1) {
					if ((tag.equals("NM1") || tag.equals("HL")) && hasPrevious) {
						generateIndent(--numTabs, writer);
						writer.println("</" + generateLoopCode(1) + ">");
					}
					if (tag.equals("NM1")) {
						if (hasPrevious == true) { // If it has a previous, it'll update
							loopCode1.replace(loopCode1.firstEntry().getKey(), alphaConv.get(loopCode1.firstEntry().getValue()));
						}
						generateIndent(numTabs++, writer);
						writer.println("<" + generateLoopCode(1) + ">");
						hasPrevious = true;
					}
					if (tag.equals("HL")) {
						level++;
						initLoopCodes(level);
						hasPrevious = false;
					}
				}
				
				if (level == 2) {
					if ((tag.equals("NM1") || tag.equals("CLM") ||tag.equals("HL")) && activeLoop3) {
						generateIndent(--numTabs, writer);
						writer.println("</" + generateLoopCode(3) + ">");
					}
					
					if ((tag.equals("CLM") ||tag.equals("HL")) && activeLoop2) {
						generateIndent(--numTabs, writer);
						writer.println("</" + generateLoopCode(2) + ">");
					}
					
					if ((tag.equals("CLM") ||tag.equals("HL")) && hasPrevious) {
						generateIndent(--numTabs, writer);
						writer.println("</" + generateLoopCode(1) + ">");
					}
					
					if (tag.equals("HL")) {
						if (hasPrevious == true) { // If it has a previous, it'll update
							loopCode1.replace(loopCode1.firstEntry().getKey(), alphaConv.get(loopCode1.firstEntry().getValue()));
							loopCode2.replace(loopCode2.firstEntry().getKey(), alphaConv.get(loopCode2.firstEntry().getValue()));
							activeLoop2 = false;
							activeLoop3 = false;
							loopCode3.replace(loopCode3.firstEntry().getKey(), 'A');
							
						}
						generateIndent(numTabs++, writer);
						writer.println("<" + generateLoopCode(1) + ">");
						hasPrevious = true;
					}
					if (tag.equals("NM1") && activeLoop2 == false) {
						generateIndent(numTabs++, writer);
						writer.println("<" + generateLoopCode(2) + ">");
						activeLoop2 = true;
					}
					if (tag.equals("NM1")) {
						if (activeLoop3 == true) {
							loopCode3.replace(loopCode3.firstEntry().getKey(), alphaConv.get(loopCode3.firstEntry().getValue()));
						}
						generateIndent(numTabs++, writer);
						writer.println("<" + generateLoopCode(3) + ">");
						activeLoop3 = true;
					}
					if (tag.equals("CLM")) {
						level++;
						initLoopCodes(level);
					}
				}
				
				if (level == 3) {
					if ((tag.equals("NM1") || tag.equals("LX")) && activeLoop3) {
						generateIndent(--numTabs, writer);
						writer.println("</" + generateLoopCode(3) + ">");
					}
					
					if ((tag.equals("CLM") ||tag.equals("LX")) && activeLoop2) {
						generateIndent(--numTabs, writer);
						writer.println("</" + generateLoopCode(2) + ">");
					}
					
					if ((tag.equals("CLM") ||tag.equals("LX")) && hasPrevious) {
						generateIndent(--numTabs, writer);
						writer.println("</" + generateLoopCode(1) + ">");
					}
					
					if (tag.equals("CLM")) {
						if (hasPrevious == true) { // If it has a previous, it'll update
							loopCode1.replace(loopCode1.firstEntry().getKey(), alphaConv.get(loopCode1.firstEntry().getValue()));
							loopCode2.replace(loopCode2.firstEntry().getKey(), alphaConv.get(loopCode2.firstEntry().getValue()));
							activeLoop2 = false;
							activeLoop3 = false;
							loopCode3.replace(loopCode3.firstEntry().getKey(), 'A');
							
						}
						generateIndent(numTabs++, writer);
						writer.println("<" + generateLoopCode(1) + ">");
						hasPrevious = true;
					}
					if (tag.equals("NM1") && activeLoop2 == false) {
						generateIndent(numTabs++, writer);
						writer.println("<" + generateLoopCode(2) + ">");
						activeLoop2 = true;
					}
					
					if (tag.equals("NM1")) {
						if (activeLoop3 == true) {
							loopCode3.replace(loopCode3.firstEntry().getKey(), alphaConv.get(loopCode3.firstEntry().getValue()));
						}
						generateIndent(numTabs++, writer);
						writer.println("<" + generateLoopCode(3) + ">");
						activeLoop3 = true;
					}
					if (tag.equals("LX")) {
						level++;
						initLoopCodes(level);
					}
				}
				
				if (level == 4) {
					if (tag.equals("LX") && hasPrevious) {
						generateIndent(--numTabs, writer);
						writer.println("</" + generateLoopCode(1) + ">");
					}
					if (tag.equals("LX")) {
						if (hasPrevious == true) { // If it has a previous, it'll update
							loopCode1.replace(loopCode1.firstEntry().getKey(), alphaConv.get(loopCode1.firstEntry().getValue()));
						}
						generateIndent(numTabs++, writer);
						writer.println("<" + generateLoopCode(1) + ">");
						hasPrevious = true;
					}
				}
				
				values[values.length-1] = values[values.length-1].substring(0, values[values.length-1].length()-1);
				for (String index : values) {
					if (index.equals(tag)) {
						generateIndent(numTabs++, writer);
						writer.println("<" + tag + " segIdx = \"" + idCounter++ + "\">");
					} else {
						generateIndent(numTabs, writer);
						String subtag = tag + generateElemIndex(elementIndex++);
						writer.println("<" + subtag+ ">" + index + "</" + subtag + ">");
					}
				}
				generateIndent(--numTabs, writer);
				writer.println("</" + tag + ">");
			}
		}
		writer.println("</ConnorData>");
		writer.close();
	}

	private static void generateIndent(int numIndent, PrintWriter fw) {
		for (int x = 0; x < numIndent; x++) {
			fw.print("	");
		}
	}
	
	private static String generateElemIndex(int index) {
		if (index < 10) {
			return "0" + index;
		} else {
			return Integer.toString(index);
		}
	}
	
	private static String generateLoopCode(int level) {
		if (level == 1) {
			return "Loop" + loopCode1.firstEntry().getKey().toString() + loopCode1.firstEntry().getValue().toString();
		} else if (level == 2) {
			return "Loop" + loopCode2.firstEntry().getKey().toString() + loopCode2.firstEntry().getValue().toString();
		} else if (level == 3) {
			String entry = loopCode3.firstEntry().getValue().toString();
			return "Loop" + loopCode3.firstEntry().getKey().toString() + loopCode2.firstEntry().getValue().toString() + entry;
		} else {
			throw new IllegalArgumentException("Invalid level");
		}
	}
	
	private static void initLoopCodes() {
		loopCode1.put(1000, 'A');
		loopCode2.put(1000, 'A');
		loopCode3.put(1000, 'A');
		hasPrevious = false;
		activeLoop2 = false;
		activeLoop3 = false;
		level = 1;
	}
	
	private static void initLoopCodes(int level) {
		clearLoopCodes();
		hasPrevious = false;
		activeLoop2 = false;
		activeLoop3 = false;
		if (level == 2) {
			loopCode1.put(2000, 'A');
			loopCode2.put(2010, 'A');
			loopCode3.put(2010, 'A');
		} else if (level == 3) {
			loopCode1.put(2300, 'A');
			loopCode2.put(2310, 'A');
			loopCode3.put(2310, 'A');
		} else if (level == 4) {
		loopCode1.put(2400, 'A');
		loopCode2.put(2400, 'A');
		loopCode3.put(2400, 'A');
		} else {
			throw new IllegalArgumentException("Invalid level");
		}
	}
	
	private static void clearLoopCodes() {
		loopCode1.clear();
		loopCode2.clear();
		loopCode3.clear();
	}
	
	private static void restartLoopCodes() {
		clearLoopCodes();
		initLoopCodes();
	}
	
	private static void initChar() {
		alphaConv.put('A', 'B');
		alphaConv.put('B', 'C');
		alphaConv.put('C', 'D');
		alphaConv.put('D', 'E');
		alphaConv.put('E', 'F');
		alphaConv.put('F', 'G');
		alphaConv.put('G', 'H');
		alphaConv.put('H', 'I');
		alphaConv.put('I', 'J');
		alphaConv.put('J', 'K');
		alphaConv.put('K', 'L');
		alphaConv.put('L', 'M');
		alphaConv.put('M', 'N');
		alphaConv.put('N', 'O');
		alphaConv.put('O', 'P');
		alphaConv.put('P', 'Q');
		alphaConv.put('Q', 'R');
		alphaConv.put('R', 'S');
		alphaConv.put('S', 'T');
		alphaConv.put('T', 'U');
		alphaConv.put('U', 'V');
		alphaConv.put('V', 'W');
		alphaConv.put('W', 'X');
		alphaConv.put('X', 'Y');
		alphaConv.put('Y', 'Z');
	}

	public static void main(String[] args) {
		try {
			process("x12.txt");
		} catch (FileNotFoundException e) {
			System.out.println("Invalid file");
		} catch (IOException e) {
			System.out.println("IO Exception");
		}
	}

}
