import React, { useState } from 'react';
import {
  Substance,
  BktSkillState,
  ExperimentLog,
  ExperimentTemplate,
  StudentSample,
} from './types';
import { ALL_SUBSTANCES } from './data/substances';
import { INITIAL_BKT_SKILLS, updateBktProbability } from './engine/bktEngine';
import { STUDENT_SAMPLES_60 } from './data/researchData';
import { LabScreen } from './screens/LabScreen';
import { CatalogScreen } from './screens/CatalogScreen';
import { PersonalizedDashboardScreen } from './screens/PersonalizedDashboardScreen';
import { KhktResearchScreen } from './screens/KhktResearchScreen';
import { SocraticChatModal } from './components/SocraticChatModal';
import { GalvanicCellModal } from './components/GalvanicCellModal';
import { SafetyProtocolModal } from './components/SafetyProtocolModal';
import { audioEngine } from './utils/audioEngine';
import {
  FlaskConical,
  BookOpen,
  Brain,
  Award,
  Globe,
  MessageSquare,
  Sparkles,
  Volume2,
  VolumeX,
  BatteryCharging,
  ShieldAlert,
} from 'lucide-react';

export const App: React.FC = () => {
  // Navigation tabs
  const [activeTab, setActiveTab] = useState<'LAB' | 'CATALOG' | 'DASHBOARD' | 'KHKT'>('LAB');

  // Curriculum IUPAC vs Vietnamese mode
  const [isIupacMode, setIsIupacMode] = useState(false);

  // Audio synthesizer sound toggle
  const [isSoundOn, setIsSoundOn] = useState(audioEngine.isEnabled());

  // Additional feature modals: Galvanic Cell & Safety Protocol
  const [isGalvanicOpen, setIsGalvanicOpen] = useState(false);
  const [isSafetyOpen, setIsSafetyOpen] = useState(false);

  // Lab Reactants State
  const [reactantA, setReactantA] = useState<Substance | null>(
    ALL_SUBSTANCES.find((s) => s.id === 'zn') || null
  );
  const [reactantB, setReactantB] = useState<Substance | null>(
    ALL_SUBSTANCES.find((s) => s.id === 'hcl') || null
  );

  // Socratic Modal State
  const [isSocraticOpen, setIsSocraticOpen] = useState(false);
  const [socraticPrompt, setSocraticPrompt] = useState<string | undefined>(undefined);

  // BKT Competencies State
  const [bktSkills, setBktSkills] = useState<BktSkillState[]>(INITIAL_BKT_SKILLS);

  // Student Experiment History (Pre-populated with 5 recent experiments across various outcomes)
  const [logs, setLogs] = useState<ExperimentLog[]>([
    {
      id: 'log-init-1',
      title: 'Zn + 2HCl → ZnCl2 + H2↑',
      timestamp: '10:15',
      reactants: 'Zn + HCl',
      reactantAId: 'zn',
      reactantBId: 'hcl',
      status: 'SUCCESS',
      hazardType: 'NONE',
      hypothesisCorrect: true,
      studentExplanation:
        'Kẽm có tính khử mạnh hơn hidro trong dãy điện hóa, nhường 2e cho 2H+ tạo thành bọt khí H2.',
      aiEvaluation:
        '✓ Đạt chuẩn GDPT 2018! Em giải thích chính xác bản chất thế điện cực E°(Zn²⁺/Zn) = -0.76V.',
      competencyTarget: 'metal_series',
    },
    {
      id: 'log-init-2',
      title: 'Na + H2O → 2NaOH + H2↑ (Vi phạm nổ)',
      timestamp: '10:08',
      reactants: 'Na + H2O',
      reactantAId: 'na',
      reactantBId: 'h2o',
      status: 'HAZARD_VIOLATION',
      hazardType: 'EXPLOSION',
      hypothesisCorrect: false,
      studentExplanation:
        'Thả mẩu natri lớn vào cốc nước gây phản ứng mãnh liệt, natri nóng chảy và nổ bốc cháy ngọn lửa vàng.',
      aiEvaluation:
        '⚠️ Vi phạm an toàn! Cần dùng kính bảo hộ, kẹp gắp và chỉ lấy mẩu Na nhỏ bằng hạt gạo.',
      competencyTarget: 'redox_micro',
    },
    {
      id: 'log-init-3',
      title: 'Cu + 2HCl → Không phản ứng',
      timestamp: '09:54',
      reactants: 'Cu + HCl',
      reactantAId: 'cu',
      reactantBId: 'hcl',
      status: 'SUCCESS',
      hazardType: 'NONE',
      hypothesisCorrect: true,
      studentExplanation:
        'Đồng đứng sau hidro trong dãy hoạt động hóa học (E° = +0.34V) nên không thể khử H+ từ axit thường.',
      aiEvaluation:
        '✓ Xuất sắc! Nhận định chính xác quy luật phản ứng giữa kim loại và axit clohidric.',
      competencyTarget: 'metal_series',
    },
    {
      id: 'log-init-4',
      title: 'BaCl2 + H2SO4 → BaSO4↓ + 2HCl',
      timestamp: '09:40',
      reactants: 'BaCl2 + H2SO4',
      reactantAId: 'bacl2',
      reactantBId: 'h2so4',
      status: 'SUCCESS',
      hazardType: 'NONE',
      hypothesisCorrect: true,
      studentExplanation:
        'Phản ứng trao đổi ion trong dung dịch tạo kết tủa trắng BaSO4 bền không tan trong axit mạnh.',
      aiEvaluation:
        '✓ Chính xác! BaSO4 là dấu hiệu đặc trưng nhận biết cation Ba²⁺ và anion SO4²⁻.',
      competencyTarget: 'ion_exchange',
    },
    {
      id: 'log-init-5',
      title: 'H2O + H2SO4 đặc (Sôi bùng Exothermic)',
      timestamp: '09:25',
      reactants: 'H2O + H2SO4_conc',
      reactantAId: 'h2o',
      reactantBId: 'h2so4_conc',
      status: 'HAZARD_VIOLATION',
      hazardType: 'EXOTHERMIC_BOIL',
      hypothesisCorrect: false,
      studentExplanation:
        'Rót nước vào axit đặc gây sôi bùng bắn tung tóe axit ăn mòn nguy hiểm.',
      aiEvaluation:
        '🚨 Cảnh báo an toàn: Phải rót từ từ axit đặc vào nước dọc theo đũa thủy tinh, tuyệt đối không làm ngược lại!',
      competencyTarget: 'enthalpy_energy',
    },
  ]);

  // Handle BKT Update when an experiment or quiz is completed
  const handleRecordExperiment = (
    title: string,
    isCorrect: boolean,
    studentExplanation: string,
    aiFeedback: string,
    competencyId: string
  ) => {
    // 1. Update BKT skill
    setBktSkills((prev) =>
      prev.map((skill) => {
        if (skill.competencyId === competencyId) {
          const newProb = updateBktProbability(skill.currentProb, isCorrect);
          return {
            ...skill,
            currentProb: newProb,
            totalAttempts: skill.totalAttempts + 1,
            correctCount: isCorrect ? skill.correctCount + 1 : skill.correctCount,
          };
        }
        return skill;
      })
    );

    // 2. Add to logs with status and reactants
    const isExothermicBoilHazard =
      (reactantA?.id === 'h2o' && reactantB?.id === 'h2so4_conc') ||
      (reactantA?.id === 'h2so4_conc' && reactantB?.id === 'h2o');
    const isExplosionHazard =
      (reactantA?.id === 'na' && reactantB?.id === 'h2o') ||
      (reactantA?.id === 'h2o' && reactantB?.id === 'na');

    const hazardType = isExothermicBoilHazard
      ? 'EXOTHERMIC_BOIL'
      : isExplosionHazard
      ? 'EXPLOSION'
      : 'NONE';

    const status = hazardType !== 'NONE'
      ? 'HAZARD_VIOLATION'
      : isCorrect
      ? 'SUCCESS'
      : 'FAILURE';

    const newLog: ExperimentLog = {
      id: `log-${Date.now()}`,
      title,
      timestamp: new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }),
      reactants: `${reactantA?.formula || 'A'} + ${reactantB?.formula || 'B'}`,
      reactantAId: reactantA?.id,
      reactantBId: reactantB?.id,
      status,
      hazardType,
      hypothesisCorrect: isCorrect,
      studentExplanation,
      aiEvaluation: aiFeedback,
      competencyTarget: competencyId,
    };
    setLogs((prev) => [newLog, ...prev]);
  };

  const handleReloadExperiment = (reactantAId?: string, reactantBId?: string) => {
    if (reactantAId) {
      const foundA = ALL_SUBSTANCES.find((s) => s.id === reactantAId) || null;
      setReactantA(foundA);
    }
    if (reactantBId) {
      const foundB = ALL_SUBSTANCES.find((s) => s.id === reactantBId) || null;
      setReactantB(foundB);
    }
    setActiveTab('LAB');
  };

  const handleAnswerQuiz = (competencyId: string, isCorrect: boolean) => {
    setBktSkills((prev) =>
      prev.map((skill) => {
        if (skill.competencyId === competencyId) {
          const newProb = updateBktProbability(skill.currentProb, isCorrect);
          return {
            ...skill,
            currentProb: newProb,
            totalAttempts: skill.totalAttempts + 1,
            correctCount: isCorrect ? skill.correctCount + 1 : skill.correctCount,
          };
        }
        return skill;
      })
    );
  };

  const handleSelectFromCatalog = (template: ExperimentTemplate) => {
    const subA = ALL_SUBSTANCES.find((s) => s.id === template.reactantAId) || null;
    const subB = ALL_SUBSTANCES.find((s) => s.id === template.reactantBId) || null;
    setReactantA(subA);
    setReactantB(subB);
    setActiveTab('LAB');
  };

  return (
    <div className="min-h-screen bg-[#070e1c] text-slate-100 flex flex-col font-sans selection:bg-cyan-500 selection:text-slate-950">
      {/* Top Main Navigation Header */}
      <header className="sticky top-0 z-40 bg-[#081224]/90 backdrop-blur-md border-b border-cyan-500/20 px-4 py-3">
        <div className="max-w-7xl mx-auto flex items-center justify-between gap-2">
          {/* Logo & Project Title */}
          <div
            onClick={() => setActiveTab('LAB')}
            className="flex items-center gap-2.5 cursor-pointer group"
          >
            <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-cyan-500 to-emerald-400 p-0.5 shadow-lg shadow-cyan-500/20">
              <div className="w-full h-full bg-[#081224] rounded-[10px] flex items-center justify-center">
                <FlaskConical className="w-5 h-5 text-cyan-400 group-hover:scale-110 transition-transform" />
              </div>
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="font-extrabold text-sm sm:text-base tracking-tight text-white flex items-center gap-1.5">
                  <span>Smart ChemLab</span>
                  <span className="text-[10px] font-bold px-1.5 py-0.2 rounded bg-cyan-950 text-cyan-400 border border-cyan-500/40">
                    GDPT 2018
                  </span>
                </h1>
              </div>
              <p className="text-[10px] text-slate-400 hidden sm:block">
                Phòng Thí nghiệm Ảo Thích ứng • Socratic AI • Đánh giá BKT
              </p>
            </div>
          </div>

          {/* Navigation Tabs */}
          <nav className="flex items-center gap-1 bg-slate-900/90 p-1 rounded-xl border border-slate-800">
            {[
              { id: 'LAB', label: 'Phòng Lab', icon: FlaskConical },
              { id: 'CATALOG', label: 'Ngân hàng', icon: BookOpen },
              { id: 'DASHBOARD', label: 'Năng lực BKT', icon: Brain },
              { id: 'KHKT', label: 'Báo cáo KHKT', icon: Award },
            ].map((tab) => {
              const Icon = tab.icon;
              const isActive = activeTab === tab.id;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id as any)}
                  className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                    isActive
                      ? 'bg-cyan-500 text-slate-950 shadow-md shadow-cyan-500/20'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                  }`}
                >
                  <Icon className="w-3.5 h-3.5" />
                  <span className="hidden md:inline">{tab.label}</span>
                </button>
              );
            })}
          </nav>

          {/* Settings, Audio & Socratic Trigger */}
          <div className="flex items-center gap-2">
            {/* Audio Synthesizer Toggle */}
            <button
              onClick={() => {
                const nextState = !isSoundOn;
                setIsSoundOn(nextState);
                audioEngine.setEnabled(nextState);
              }}
              className="flex items-center gap-1 px-2.5 py-1.5 rounded-xl bg-slate-900 border border-slate-800 hover:border-slate-700 text-[11px] font-semibold transition-colors cursor-pointer text-slate-300"
              title={isSoundOn ? 'Âm thanh: Đang bật (Nhấn để tắt)' : 'Âm thanh: Đã tắt (Nhấn để bật)'}
            >
              {isSoundOn ? (
                <Volume2 className="w-3.5 h-3.5 text-cyan-400" />
              ) : (
                <VolumeX className="w-3.5 h-3.5 text-slate-500" />
              )}
              <span className="hidden sm:inline">{isSoundOn ? 'Âm thanh' : 'Tắt tiếng'}</span>
            </button>

            {/* IUPAC Nomenclature Toggle */}
            <button
              onClick={() => setIsIupacMode(!isIupacMode)}
              className="flex items-center gap-1 px-2.5 py-1.5 rounded-xl bg-slate-900 border border-slate-800 hover:border-slate-700 text-[11px] text-slate-300 font-semibold transition-colors cursor-pointer"
              title="Chuyển đổi danh pháp IUPAC Quốc tế / Tiếng Việt GDPT 2018"
            >
              <Globe className="w-3.5 h-3.5 text-cyan-400" />
              <span className="hidden sm:inline">{isIupacMode ? 'IUPAC' : 'GDPT 2018'}</span>
            </button>

            {/* Socratic Assistant Button */}
            <button
              onClick={() => setIsSocraticOpen(true)}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-gradient-to-r from-amber-500 to-cyan-500 hover:from-amber-400 hover:to-cyan-400 text-slate-950 font-bold text-xs shadow-md shadow-cyan-500/20 transition-all cursor-pointer"
            >
              <Sparkles className="w-3.5 h-3.5 fill-slate-950" />
              <span className="hidden sm:inline">Hỏi Socratic</span>
            </button>
          </div>
        </div>
      </header>

      {/* Screen Routing */}
      <main className="flex-1">
        {activeTab === 'LAB' && (
          <LabScreen
            reactantA={reactantA}
            reactantB={reactantB}
            onSelectA={setReactantA}
            onSelectB={setReactantB}
            onClearReactants={() => {
              setReactantA(null);
              setReactantB(null);
            }}
            onRecordExperiment={handleRecordExperiment}
            onOpenSocratic={() => {
              setSocraticPrompt(undefined);
              setIsSocraticOpen(true);
            }}
            onOpenSocraticWithPrompt={(prompt) => {
              setSocraticPrompt(prompt);
              setIsSocraticOpen(true);
            }}
            isIupacMode={isIupacMode}
            logs={logs}
            skills={bktSkills}
            onReloadExperiment={handleReloadExperiment}
            onOpenGalvanic={() => setIsGalvanicOpen(true)}
            onOpenSafety={() => setIsSafetyOpen(true)}
          />
        )}

        {activeTab === 'CATALOG' && (
          <CatalogScreen
            onSelectExperiment={handleSelectFromCatalog}
            isIupacMode={isIupacMode}
          />
        )}

        {activeTab === 'DASHBOARD' && (
          <PersonalizedDashboardScreen
            skills={bktSkills}
            logs={logs}
            onAnswerQuiz={handleAnswerQuiz}
          />
        )}

        {activeTab === 'KHKT' && (
          <KhktResearchScreen students={STUDENT_SAMPLES_60} />
        )}
      </main>

      {/* Socratic AI Chat Modal */}
      <SocraticChatModal
        isOpen={isSocraticOpen}
        onClose={() => {
          setIsSocraticOpen(false);
          setSocraticPrompt(undefined);
        }}
        experimentContext={`${reactantA?.formula || 'Chưa chọn'} + ${reactantB?.formula || 'Chưa chọn'}`}
        studentHypothesis="Đang tiến hành thí nghiệm trong phòng Lab"
        safetyViolationPrompt={socraticPrompt}
      />

      {/* Electrochemical Galvanic Cell Modal */}
      <GalvanicCellModal
        isOpen={isGalvanicOpen}
        onClose={() => setIsGalvanicOpen(false)}
        isIupacMode={isIupacMode}
      />

      {/* Lab Safety & First-Aid Protocol Guide */}
      <SafetyProtocolModal
        isOpen={isSafetyOpen}
        onClose={() => setIsSafetyOpen(false)}
        onOpenSocratic={(prompt) => {
          setSocraticPrompt(prompt);
          setIsSocraticOpen(true);
        }}
      />
    </div>
  );
};

export default App;
