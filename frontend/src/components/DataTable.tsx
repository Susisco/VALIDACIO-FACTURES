import React, { useState } from 'react';

export interface Column<T> {
  key: keyof T | 'actions'; // Permite 'actions' como clave adicional
  header: string;
  render?: (item: T) => React.ReactNode;
}

interface DataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  pageSize?: number;
}

export function DataTable<T extends object>({
  columns,
  data,
  pageSize = 10
}: DataTableProps<T>) {
  const [page, setPage] = useState(0);
  const [filter, setFilter] = useState('');

  // 1. Filtrat simple: text en qualsevol columna textual
  const filtered = data.filter(item =>
    columns.some(col => {
      const value = col.key !== 'actions' ? String(item[col.key] ?? '').toLowerCase() : '';
      return value.includes(filter.toLowerCase());
    })
  );

  // 2. Paginació
  const totalPages = Math.ceil(filtered.length / pageSize);
  const pageData = filtered.slice(page * pageSize, page * pageSize + pageSize);

  return (
    <div className="w-full">
      {/* Filtre */}
      <div className="mb-4">
        <input
          type="text"
          placeholder="Filtra..."
          className="w-full p-2 border rounded"
          value={filter}
          onChange={e => {
            setFilter(e.target.value);
            setPage(0);
          }}
        />
      </div>

      {/* Taula */}
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            {columns.map(col => (
              <th
                key={String(col.key)}
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                {col.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {pageData.map((row, rowIndex) => (
            <tr key={rowIndex}>
              {columns.map(col => (
                <td
                  key={String(col.key)}
                  className="px-6 py-4 whitespace-nowrap text-sm text-gray-700"
                >
                  {col.render ? col.render(row) : col.key !== 'actions' ? String(row[col.key] ?? '') : null}
                </td>
              ))}
            </tr>
          ))}
          {pageData.length === 0 && (
            <tr>
              <td colSpan={columns.length} className="px-6 py-4 text-center text-sm text-gray-500">
                No hi ha dades per mostrar
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {/* Controls de paginació */}
      <div className="mt-4 flex items-center justify-end space-x-2">
        <button
          className="px-3 py-1 border rounded disabled:opacity-50"
          onClick={() => setPage(p => Math.max(p - 1, 0))}
          disabled={page === 0}
        >
          Anterior
        </button>
        <span className="text-sm">
          {page + 1} / {totalPages || 1}
        </span>
        <button
          className="px-3 py-1 border rounded disabled:opacity-50"
          onClick={() => setPage(p => Math.min(p + 1, totalPages - 1))}
          disabled={page + 1 >= totalPages}
        >
          Següent
        </button>
      </div>
    </div>
  );
}
