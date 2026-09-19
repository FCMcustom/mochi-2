import React, { useState } from 'react';
import { ExperimentLog } from '../types';
import {
  History,
  CheckCircle2,
  AlertTriangle,
  XCircle,
  Clock,
  ChevronDown,
  ChevronUp,
  RotateCcw,
  FileDown,
  Sparkles,
  ExternalLink
} from 'lucide-react';

interface RecentExperimentsDrawerProps {
  logs: ExperimentLog[];
  onReloadExperiment?: (reactantAId?: string, reactantBId?: string) => void;
  onExportPdf?: () => void;
}

export const RecentExperimentsDrawer: React.FC<RecentExperimentsDrawerProps> = ({
  logs,
  onReloadExperiment,
  onExportPdf,
}) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [expandedLogId, setExpandedLogId] = useState<string | null>(null);

  // Take the 5 most recent experiments
  const recent5 = logs.slice(0, 5);

  const getStatusBadge = (log: ExperimentLog) => {
    if (log.status === 'HAZARD_VIOLATION' || (log.hazardType && log.hazardType !== 'NONE')) {
      return (
        <span className="flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-bold bg-rose-500/20 text-rose-300 border border-rose-500/40">
          <AlertTriangle className="w-3 h-3 text-rose-400" />
          <span>Vi phạm An toàn</span>
        </span>
      );
    }
    if (log.status === 'SUCCESS' || log.hypothesisCorrect) {
      return (
        <span className="flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-bold bg-emerald-500/20 text-emerald-300 border border-emerald-500/40">
          <CheckCircle2 className="w-3 h-3 text-emerald-400" />
          <span>Thành công</span>
        </span>
      );
    }
    return (
      <span className="flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-bold bg-amber-500/20 text-amber-300 border border-amber-500/40">
        <XCircle className="w-3 h-3 text-amber-400" />
        <span>Thất bại (Giả thuyết)</span>
      </span>
    );
  };

  return (
    <div className="w-full bg-[#091528] border border-cyan-500/30 rounded-2xl overflow-hidden shadow-xl transition-all">
      {/* Drawer Header Toggle Bar */}
      <div
        onClick={() => setIsExpanded(!isExpanded)}
        className="px-4 py-3 bg-[#0a1830] hover:bg-[#0d2040] cursor-pointer flex items-center justify-between transition-colors select-none"
      >
        <div className="flex items-center gap-2.5">
          <div className="w-7 h-7 rounded-lg bg-cyan-500/20 border border-cyan-500/40 flex items-center justify-center">
            <History className="w-4 h-4 text-cyan-400" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h4 className="font-bold text-xs sm:text-sm text-white">
                NHẬT KÝ 5 THÍ NGHIỆM GẦN ĐÂY NHẤT
              </h4>
              <span className="text-[10px] font-extrabold px-2 py-0.5 rounded-full bg-cyan-950 text-cyan-300 border border-cyan-500/40">
                {recent5.length} / 5
              </span>
            </div>
            <p className="text-[10px] text-slate-400 hidden sm:block">
              Theo dõi trạng thái thành công/thất bại, thời gian thực hiện và tái sử dụng nhanh hóa chất
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          {onExportPdf && (
            <button
              onClick={(e) => {
                e.stopPropagation();
                onExportPdf();
              }}
              className="flex items-center gap-1 px-2.5 py-1 rounded-lg bg-cyan-500/20 hover:bg-cyan-500/30 border border-cyan-500/40 text-cyan-300 text-[11px] font-bold transition-all shadow-sm"
              title="Xuất báo cáo PDF 1 trang cho phiên lab này"
            >
              <FileDown className="w-3.5 h-3.5" />
              <span className="hidden sm:inline">Xuất PDF Lab</span>
            </button>
          )}

          <button className="p-1 rounded-lg text-slate-400 hover:text-white">
            {isExpanded ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
          </button>
        </div>
      </div>

      {/* Collapsible Content */}
      {isExpanded && (
        <div className="p-3 sm:p-4 flex flex-col gap-2.5 border-t border-slate-800/80 bg-[#071122]/95">
          {recent5.length === 0 ? (
            <div className="text-center py-6 text-xs text-slate-400 flex flex-col items-center gap-1.5">
              <Clock className="w-5 h-5 text-slate-500" />
              <span>Chưa có thí nghiệm nào được lưu trong phiên hiện tại.</span>
              <span className="text-[11px] text-slate-500">
                Sau khi kích hoạt phản ứng ở Bước 2 & giải thích ở Bước 4, nhật ký sẽ tự động xuất hiện tại đây.
              </span>
            </div>
          ) : (
            recent5.map((log, index) => {
              const isDetailsOpen = expandedLogId === log.id;
              return (
                <div
                  key={log.id}
                  className="bg-slate-900/80 hover:bg-slate-900 border border-slate-800 rounded-xl p-3 flex flex-col gap-2 transition-colors"
                >
                  <div className="flex items-start sm:items-center justify-between gap-2">
                    <div className="flex items-center gap-2.5 flex-wrap">
                      <span className="w-5 h-5 rounded-full bg-slate-800 text-[10px] font-mono font-bold text-slate-300 flex items-center justify-center">
                        {index + 1}
                      </span>
                      <span className="text-xs font-bold text-slate-100 font-mono">
                        {log.title}
                      </span>
                      {getStatusBadge(log)}
                    </div>

                    <div className="flex items-center gap-2 shrink-0">
                      <div className="flex items-center gap-1 text-[10px] text-slate-400 font-mono">
                        <Clock className="w-3 h-3 text-slate-500" />
                        <span>{log.timestamp}</span>
                      </div>

                      {/* Reload Reactants Button */}
                      {onReloadExperiment && (log.reactantAId || log.reactants) && (
                        <button
                          onClick={() => onReloadExperiment(log.reactantAId, log.reactantBId)}
                          className="flex items-center gap-1 px-2 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 text-[10px] font-semibold border border-slate-700 transition-colors"
                          title="Nạp lại cặp hóa chất này vào giá thí nghiệm"
                        >
                          <RotateCcw className="w-3 h-3 text-cyan-400" />
                          <span className="hidden sm:inline">Nạp lại</span>
                        </button>
                      )}

                      <button
                        onClick={() => setExpandedLogId(isDetailsOpen ? null : log.id)}
                        className="text-[10px] text-cyan-400 hover:text-cyan-300 font-semibold underline"
                      >
                        {isDetailsOpen ? 'Thu gọn' : 'Chi tiết'}
                      </button>
                    </div>
                  </div>

                  {/* Expandable Details */}
                  {isDetailsOpen && (
                    <div className="mt-1 pt-2 border-t border-slate-800/80 flex flex-col gap-1.5 text-xs text-slate-300 bg-slate-950/40 p-2.5 rounded-lg">
                      <div>
                        <strong className="text-slate-200">Hóa chất tham gia:</strong> {log.reactants}
                      </div>
                      {log.studentExplanation && (
                        <div>
                          <strong className="text-slate-200">Giải thích của học sinh:</strong>{' '}
                          <span className="text-slate-400">{log.studentExplanation}</span>
                        </div>
                      )}
                      {log.aiEvaluation && (
                        <div className="text-emerald-400/90 text-[11px] bg-emerald-950/30 p-2 rounded border border-emerald-900/40">
                          <strong className="text-emerald-300 flex items-center gap-1 mb-0.5">
                            <Sparkles className="w-3 h-3" /> Đánh giá Socratic AI:
                          </strong>
                          {log.aiEvaluation}
                        </div>
                      )}
                    </div>
                  )}
                </div>
              );
            })
          )}
        </div>
      )}
    </div>
  );
};
