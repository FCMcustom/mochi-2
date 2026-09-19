import React, { useState } from 'react';
import { BktSkillState, ExperimentLog } from '../types';
import { ADAPTIVE_QUIZ_BANK } from '../engine/bktEngine';
import { CompetencyBadges } from '../components/CompetencyBadges';
import {
  TrendingUp,
  Brain,
  Award,
  CheckCircle,
  XCircle,
  HelpCircle,
  Sparkles,
  BookOpen,
  History
} from 'lucide-react';

interface PersonalizedDashboardScreenProps {
  skills: BktSkillState[];
  logs: ExperimentLog[];
  onAnswerQuiz: (competencyId: string, isCorrect: boolean) => void;
}

export const PersonalizedDashboardScreen: React.FC<PersonalizedDashboardScreenProps> = ({
  skills,
  logs,
  onAnswerQuiz,
}) => {
  const [selectedQuestionIndex, setSelectedQuestionIndex] = useState(0);
  const [selectedAnswer, setSelectedAnswer] = useState<number | null>(null);
  const [hasSubmitted, setHasSubmitted] = useState(false);

  const currentQuiz = ADAPTIVE_QUIZ_BANK[selectedQuestionIndex];

  const handleSubmitAnswer = () => {
    if (selectedAnswer === null || hasSubmitted) return;
    setHasSubmitted(true);
    const isCorrect = selectedAnswer === currentQuiz.correctIndex;
    onAnswerQuiz(currentQuiz.competencyId, isCorrect);
  };

  const handleNextQuestion = () => {
    setSelectedAnswer(null);
    setHasSubmitted(false);
    setSelectedQuestionIndex((prev) => (prev + 1) % ADAPTIVE_QUIZ_BANK.length);
  };

  const averageMastery = skills.reduce((sum, s) => sum + s.currentProb, 0) / (skills.length || 1);

  return (
    <div className="flex flex-col gap-6 p-4 sm:p-6 max-w-6xl mx-auto">
      {/* Top Header & Global Mastery KPI */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 bg-gradient-to-r from-[#0b172d] to-[#122344] p-5 rounded-2xl border border-cyan-500/30 shadow-xl">
        <div>
          <div className="flex items-center gap-2 text-cyan-400 font-bold text-xs uppercase tracking-wider mb-1">
            <Brain className="w-4 h-4 text-cyan-400" />
            <span>MÔ HÌNH HỌC TẬP THÍCH ỨNG (BKT ENGINE)</span>
          </div>
          <h2 className="text-xl font-bold text-white">
            BẢNG ĐÁNH GIÁ NĂNG LỰC CÁ NHÂN HÓA GDPT 2018
          </h2>
          <p className="text-xs text-slate-300 mt-1 max-w-xl">
            Ứng dụng thuật toán Bayesian Knowledge Tracing theo dõi xác suất nắm vững kiến thức P(L) theo thời gian thực dựa trên từng thao tác thực hành và giải thích khoa học.
          </p>
        </div>

        <div className="flex items-center gap-3 bg-slate-900/80 p-3 rounded-xl border border-cyan-500/40">
          <div className="text-right">
            <div className="text-[10px] text-slate-400 uppercase font-bold">Chỉ số làm chủ trung bình</div>
            <div className="text-2xl font-black font-mono text-cyan-400">
              {(averageMastery * 100).toFixed(0)}%
            </div>
          </div>
          <div className="w-12 h-12 rounded-full bg-cyan-500/20 border-2 border-cyan-400 flex items-center justify-center">
            <Award className="w-6 h-6 text-cyan-300" />
          </div>
        </div>
      </div>

      {/* 4 Competencies Grid */}
      <div>
        <h3 className="text-sm font-bold text-slate-200 uppercase tracking-wide mb-3 flex items-center gap-2">
          <TrendingUp className="w-4 h-4 text-cyan-400" />
          <span>4 NĂNG LỰC HÓA HỌC TRỌNG TÂM THEO CHUẨN ĐẦU RA GDPT 2018</span>
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-3.5">
          {skills.map((s) => {
            const percent = Math.round(s.currentProb * 100);
            const statusLabel =
              percent >= 70 ? 'Thành thạo (Mastered)' : percent >= 40 ? 'Đang phát triển' : 'Cần bồi dưỡng';
            const statusColor =
              percent >= 70
                ? 'text-emerald-400 bg-emerald-950/60 border-emerald-500/40'
                : percent >= 40
                ? 'text-cyan-400 bg-cyan-950/60 border-cyan-500/40'
                : 'text-amber-400 bg-amber-950/60 border-amber-500/40';

            return (
              <div
                key={s.competencyId}
                className="bg-[#0b172d] border border-slate-800 p-4 rounded-2xl flex flex-col justify-between shadow-lg"
              >
                <div>
                  <div className="flex items-start justify-between gap-2 mb-1.5">
                    <h4 className="font-bold text-sm text-slate-100">{s.nameVi}</h4>
                    <span className={`text-[10px] font-bold px-2 py-0.5 rounded border ${statusColor}`}>
                      {statusLabel}
                    </span>
                  </div>

                  <p className="text-xs text-slate-400 mb-3 leading-relaxed">
                    {s.descriptionVi}
                  </p>
                </div>

                <div>
                  {/* Progress Bar */}
                  <div className="flex items-center justify-between text-xs font-mono mb-1">
                    <span className="text-slate-400">Xác suất làm chủ P(L):</span>
                    <span className="font-bold text-cyan-300">{percent}%</span>
                  </div>
                  <div className="w-full bg-slate-800 h-2 rounded-full overflow-hidden">
                    <div
                      className="bg-gradient-to-r from-cyan-500 to-emerald-400 h-full transition-all duration-500"
                      style={{ width: `${percent}%` }}
                    />
                  </div>

                  <div className="flex items-center justify-between text-[11px] text-slate-500 mt-2">
                    <span>Số lần thực nghiệm: {s.totalAttempts}</span>
                    <span>Đúng chuẩn: {s.correctCount}/{s.totalAttempts}</span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Competency & Experiment Badges Gamification */}
      <CompetencyBadges skills={skills} logs={logs} />

      {/* Adaptive Drill Quiz Section */}
      <div className="bg-[#0b172d] border border-cyan-500/30 p-5 rounded-2xl shadow-xl flex flex-col gap-4">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <div className="flex items-center gap-2">
            <BookOpen className="w-4 h-4 text-amber-400" />
            <h3 className="font-bold text-sm text-slate-100 uppercase tracking-wide">
              LUYỆN TẬP BỔ TRỢ THÍCH ỨNG (ADAPTIVE DRILL QUIZ)
            </h3>
          </div>
          <span className="text-xs text-slate-400">
            Câu {selectedQuestionIndex + 1} / {ADAPTIVE_QUIZ_BANK.length}
          </span>
        </div>

        {/* Question Text */}
        <div className="text-sm font-semibold text-cyan-200 leading-relaxed">
          {currentQuiz.questionVi}
        </div>

        {/* Options */}
        <div className="flex flex-col gap-2">
          {currentQuiz.optionsVi.map((opt, idx) => {
            const isSelected = selectedAnswer === idx;
            let btnClass = 'bg-slate-900 border-slate-800 text-slate-200 hover:bg-slate-850 hover:border-slate-700';

            if (hasSubmitted) {
              if (idx === currentQuiz.correctIndex) {
                btnClass = 'bg-emerald-950/80 border-emerald-500 text-emerald-200 font-bold';
              } else if (isSelected) {
                btnClass = 'bg-rose-950/80 border-rose-500 text-rose-200';
              }
            } else if (isSelected) {
              btnClass = 'bg-cyan-950/80 border-cyan-400 text-cyan-200 ring-1 ring-cyan-400';
            }

            return (
              <button
                key={idx}
                disabled={hasSubmitted}
                onClick={() => setSelectedAnswer(idx)}
                className={`p-3 rounded-xl border text-left text-xs transition-all flex items-start gap-2.5 ${btnClass}`}
              >
                <span className="font-bold shrink-0">{String.fromCharCode(65 + idx)}.</span>
                <span className="leading-snug">{opt.slice(3)}</span>
              </button>
            );
          })}
        </div>

        {/* Explanation upon submission */}
        {hasSubmitted && (
          <div className="p-3.5 rounded-xl bg-slate-950/80 border border-cyan-500/40 text-xs flex flex-col gap-1.5 animate-fadeIn">
            <div className="flex items-center gap-1.5 font-bold">
              {selectedAnswer === currentQuiz.correctIndex ? (
                <span className="text-emerald-400 flex items-center gap-1">
                  <CheckCircle className="w-4 h-4" /> Chính xác! Năng lực BKT đã được nâng cao.
                </span>
              ) : (
                <span className="text-rose-400 flex items-center gap-1">
                  <XCircle className="w-4 h-4" /> Chưa chính xác! Hãy đọc lời giải thích để rút kinh nghiệm.
                </span>
              )}
            </div>
            <p className="text-slate-300 leading-relaxed">{currentQuiz.explanationVi}</p>
          </div>
        )}

        {/* Action button */}
        <div className="flex justify-end pt-1">
          {hasSubmitted ? (
            <button
              onClick={handleNextQuestion}
              className="px-4 py-2 rounded-xl bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-bold text-xs shadow-md transition-all"
            >
              Câu hỏi tiếp theo →
            </button>
          ) : (
            <button
              disabled={selectedAnswer === null}
              onClick={handleSubmitAnswer}
              className={`px-5 py-2 rounded-xl font-bold text-xs transition-all ${
                selectedAnswer !== null
                  ? 'bg-gradient-to-r from-cyan-500 to-emerald-400 text-slate-950 shadow-md shadow-cyan-500/20'
                  : 'bg-slate-800 text-slate-500 cursor-not-allowed'
              }`}
            >
              Kiểm tra đáp án & Cập nhật BKT
            </button>
          )}
        </div>
      </div>

      {/* Experiment History Logs */}
      <div className="bg-[#0b172d] border border-slate-800 p-5 rounded-2xl shadow-xl flex flex-col gap-3">
        <div className="flex items-center gap-2 border-b border-slate-800 pb-3">
          <History className="w-4 h-4 text-cyan-400" />
          <h3 className="font-bold text-sm text-slate-100 uppercase tracking-wide">
            NHẬT KÝ THÍ NGHIỆM ĐÃ THỰC HIỆN ({logs.length})
          </h3>
        </div>

        {logs.length === 0 ? (
          <div className="text-xs text-slate-500 py-4 text-center">
            Chưa có ghi chép thí nghiệm nào. Hãy vào Phòng Lab để thực hiện phản ứng đầu tiên!
          </div>
        ) : (
          <div className="flex flex-col gap-2.5">
            {logs.map((log) => (
              <div
                key={log.id}
                className="bg-slate-900/90 border border-slate-800 p-3 rounded-xl flex flex-col gap-1.5"
              >
                <div className="flex items-center justify-between text-xs">
                  <div className="flex items-center gap-2">
                    <span className="font-bold font-mono text-cyan-300">{log.title}</span>
                    <span
                      className={`text-[10px] px-1.5 py-0.5 rounded font-bold ${
                        log.hypothesisCorrect
                          ? 'bg-emerald-950 text-emerald-300 border border-emerald-500/30'
                          : 'bg-rose-950 text-rose-300 border border-rose-500/30'
                      }`}
                    >
                      {log.hypothesisCorrect ? 'Giả thuyết đúng' : 'Giả thuyết chưa chuẩn'}
                    </span>
                  </div>
                  <span className="text-slate-500 text-[10px]">{log.timestamp}</span>
                </div>

                <div className="text-xs text-slate-300">
                  <strong>Giải thích học sinh:</strong> {log.studentExplanation}
                </div>

                <div className="text-[11px] text-slate-400 bg-slate-950/60 p-2 rounded-lg border border-slate-800/80">
                  <span className="text-amber-400 font-bold">Nhận xét Socratic:</span>{' '}
                  {log.aiEvaluation}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
