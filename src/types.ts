export type PhysicalState = 'SOLID' | 'LIQUID' | 'GAS' | 'AQUEOUS';

export type HazardType = 'NONE' | 'EXOTHERMIC_BOIL' | 'EXPLOSION' | 'TOXIC_GAS';

export interface Substance {
  id: string;
  formula: string;
  nameIupac: string;
  nameVi: string;
  physicalState: PhysicalState;
  colorHex: string;
  concentration?: string;
  hazardWarning?: string;
  isToxicOrDangerous: boolean;
  densityGml?: number;
}

export type ReactionCategory = 
  | 'METAL_DISPLACEMENT'
  | 'ION_EXCHANGE'
  | 'REDOX'
  | 'NEUTRALIZATION'
  | 'NO_REACTION';

export interface ReactionOutcome {
  reactantAId: string;
  reactantBId: string;
  balancedEquation: string;
  netIonicEquation: string;
  phenomenonVi: string;
  phenomenonEn: string;
  microExplanationVi: string;
  microExplanationEn: string;
  deltaH: number; // kJ/mol
  tempDelta: number; // degrees Celsius
  hasGas: boolean;
  gasFormula?: string;
  hasPrecipitate: boolean;
  precipitateFormula?: string;
  precipitateColor?: string;
  flameColor?: string;
  isViolent: boolean;
  requiresHeat: boolean;
  hazardType: HazardType;
  competencyTarget: string; // 'metal_series', 'redox_micro', 'ion_exchange', 'thermo_enthalpy'
}

export interface ExperimentTemplate {
  id: string;
  titleVi: string;
  titleEn: string;
  reactantAId: string;
  reactantBId: string;
  summaryVi: string;
  summaryEn: string;
  category: ReactionCategory;
  categoryLabelVi: string;
  isDangerousOrExpensive: boolean;
  expectedOutcome: ReactionOutcome;
}

export interface BktSkillState {
  competencyId: string;
  nameVi: string;
  nameEn: string;
  descriptionVi: string;
  priorL0: number;
  probLearnT: number;
  probGuessG: number;
  probSlipS: number;
  currentProb: number;
  totalAttempts: number;
  correctCount: number;
}

export interface AdaptiveQuizQuestion {
  id: string;
  competencyId: string;
  questionVi: string;
  optionsVi: string[];
  correctIndex: number;
  explanationVi: string;
}

export interface StudentSample {
  studentId: string;
  name: string;
  researchTopic: string;
  group: 'EXPERIMENTAL' | 'CONTROL';
  preScore: number;
  postScore: number;
  scoreGain: number;
  bktMasteryRate: number;
  susScore: number;
  feedbackRating: number;
}

export interface ExperimentLog {
  id: string;
  title: string;
  timestamp: string;
  reactants: string;
  reactantAId?: string;
  reactantBId?: string;
  status: 'SUCCESS' | 'FAILURE' | 'HAZARD_VIOLATION';
  hazardType?: HazardType;
  hypothesisCorrect: boolean;
  studentExplanation: string;
  aiEvaluation: string;
  competencyTarget: string;
}

export type AppTab = 'lab' | 'catalog' | 'bkt' | 'khkt';
